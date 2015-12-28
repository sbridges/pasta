/*
 * Copyright 2015 Sean Bridges. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 * 
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 * 
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY Sean Bridges ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * 
 * 
 */

package com.github.sbridges.pasta.model.ltp.tc;

import java.util.List;
import java.util.Optional;

import com.github.sbridges.pasta.PstReader;
import com.github.sbridges.pasta.io.PstIo;
import com.github.sbridges.pasta.model.ltp.bth.BTH;
import com.github.sbridges.pasta.model.ltp.hn.BClientSig;
import com.github.sbridges.pasta.model.ltp.hn.HN;
import com.github.sbridges.pasta.model.ltp.pc.HNID;
import com.github.sbridges.pasta.model.ltp.pc.Property;
import com.github.sbridges.pasta.model.ndb.BlockTrailer;
import com.github.sbridges.pasta.model.ndb.DataBlock;
import com.github.sbridges.pasta.model.ndb.NBTEntry;
import com.github.sbridges.pasta.model.ndb.NID;
import com.github.sbridges.pasta.model.ndb.XBlockUtil;
import com.github.sbridges.pasta.util.ByteUtils;

/**
 * 
 * 2.3.4 Table Context (TC)
 *
 * A Table Context represents a table with rows of columns. 
 * From an implementation perspective, a TC is a complex, 
 * composite structure that is built on top of a HN. 
 * The presence of a TC is indicated at both the NDB and LTP Layers. 
 * At the NDB Layer, a TC is indicated through one of the special
 *  NID_TYPEs, and at the LTP Layer, a value of bTypeTC for 
 *  bClientSig in the HNHEADER structure is reserved for TCs. 
 *  The underlying TC data is separated into 3 entries: 
 *  a header with Column descriptors, a RowIndex (a nested BTH), 
 *  and the actual table data (known as the Row Matrix).
 * 
 * The Row Matrix contains the actual row data for the TC. 
 * New rows are always appended to the end of the Row Matrix, 
 * which means that the rows are not sorted in any meaningful manner.
 * To provide a way to efficiently search the Row Matrix for 
 * a particular data row, each TC also contains an embedded BTH, 
 * known as the RowIndex, to provide a 32-bit "primary index" 
 * for the Row Matrix. Each 32-bit value is a key
 * that uniquely identifies a row within the Row Matrix.
 * 
 * In practice, the Row Matrix is usually stored in a subnode
 * because of its typical size, but in rare cases, a TC can fit 
 * into a single data block if it is small enough. To facilitate 
 * navigation between rows, each row of data is of the same size, 
 * and the size is stored in the TCINFO header structure 
 * (section 2.3.4.1). To further help with data packing and alignment, 
 * the data values are grouped according to its corresponding
 * data size. DWORD and ULONGLONG values are grouped first, 
 * followed by WORD-sized data, and then byte-sized data. 
 * The TCINFO structure contains an array of offsets that
 * points to the starting offset of each group of data.
 * 
 * The TC also includes a construct known as a Cell Existence Bitmap (CEB), 
 * which is used to denote whether a particular column in a 
 * particular row actually "exists". A CEB is present at the
 * end of each row of data in the Row Matrix that indicates which columns 
 * in that row exists and which columns don't exist.
 * 
 */
public class TC {

    private final TCINFO tcInfo;
    private final NBTEntry entry;
    private final HN hn;
    private final RowIndex rowIndex;
    private final PstReader reader;
    
    private int cachedRowMatrixBlockIndex = -1;
    private PstIo cachedRowMatrix;
    
    public TC(PstReader reader, NID nid) {
        this.reader = reader;
        entry = reader.getNBT().load(nid).orElseGet(() -> { 
            throw new IllegalStateException("cant find nid" + nid);
        });
        
        hn = new HN(reader.getBBT(), entry.getBidData());
        if(hn.getHnhdr().getbClientSig() != BClientSig.bTypeTC) {
            throw new IllegalStateException("wrong type:" + hn.getHnhdr());
        }
        tcInfo = new TCINFO(hn.load(hn.getHidUserRoot()));
        
        rowIndex = new RowIndex(new BTH(hn, tcInfo.getHidRowIndex()));
        
        HNID tcInfoHnid = tcInfo.getHnidRows();
        if(tcInfoHnid.isBlank()) {
            if(rowIndex.size() != 0) {
                throw new IllegalStateException("no rows, but keys in row index");
            }
        }
        
        
        /*
         * 2.3.4.4.1 Row Data Format
         * The following is the organization of a single row of data in the Row Matrix. Rows of data are tightly- packed in the Row Matrix, and the size of each data row is TCINFO.rgib[TCI_bm] bytes. The following constraints exist for the columns within the structure.
         * Columns MUST be sorted
         */
        if(tcInfo.getTColDesc(Property.PidTagLtpRowId).getiBit() != 0 ||
           tcInfo.getTColDesc(Property.PidTagLtpRowId).getIbData() != 0  ||
           tcInfo.getTColDesc(Property.PidTagLtpRowVer).getiBit() != 1 ||
           tcInfo.getTColDesc(Property.PidTagLtpRowVer).getIbData() != 4) {
            throw new IllegalStateException();
        }
    }

    private PstIo getRowData(int row) {
        //we don't want to load a block when reading each row
        //cache the last block read
        
        //Rows per block = Floor((sizeof(block) – sizeof(BLOCKTRAILER)) / TCINFO.rgib[TCI_bm])
        //Block index = N / (rows per block)
        //Row index = N % (rows per block)
        //Each block except the last one MUST have a size of 8192 bytes.
        //If not, the file is considered corrupted. The size of a block is specified in the formula by sizeof(block).
        
        int rowPerBlock = (8192 - BlockTrailer.SIZE) / tcInfo.getTCI_bm();
        int blockIndex = row / rowPerBlock;
        int rowIndex = row % rowPerBlock;
        
        HNID tcInfoHnid = tcInfo.getHnidRows();

        if(this.cachedRowMatrixBlockIndex != blockIndex) {
            if(tcInfoHnid.isHid()) {
                if(blockIndex != 0) {
                    throw new IllegalStateException("in heap, but asking for block:" + blockIndex);
                }
                cachedRowMatrixBlockIndex = 0;
                cachedRowMatrix = hn.load(tcInfoHnid.asHID());
            } else {
                DataBlock finalBLock = XBlockUtil.getDataBlock(rowIndex, entry.getBidSub().get(),  reader.getBBT());
                cachedRowMatrix = finalBLock.getDataDecrypted();
                cachedRowMatrixBlockIndex = blockIndex;
            }
        }
        return cachedRowMatrix.slice(rowIndex * tcInfo.getTCI_bm(), tcInfo.getTCI_bm());
    }
    
    
    public List<Property<?>> getColumns() {
        return tcInfo.getProperties();
    } 
    
    public int getRowCount() {
        return rowIndex.size();
    }
    
    public List<Integer> getRowIds() {
        return rowIndex.getRowIds();
    }
    
    public <T> Optional<T> get(int rowId, Property<T> prop) {

        int index = rowIndex.getRowIndex(rowId);
        PstIo row = getRowData(index);
        
        //check the rowId, this must be at position 0
        int readRowId = row.readDw();
        if(readRowId != rowId) {
            throw new IllegalStateException("read:" + readRowId + " wanted:" + rowId);
        }
        TCOLDESC desc = tcInfo.getTColDesc(prop);

        //cell existence test
        int cbStart = tcInfo.getTCI_1b() + (desc.getiBit() / 8);
        int cbByte = 0xFF & row.readByte(cbStart);
        int bit  = cbByte >> (7 - (desc.getiBit() % 8)) & 1;
        if(bit == 0) {
            return Optional.empty();
        }
        
        row.seek(desc.getIbData());
        byte[] contents = row.read(desc.getCbData());

        
        if(prop.getType().getSize().isVariable()) {
            if(contents.length != 4) {
                throw new IllegalStateException();
            }
            HNID hnid = new HNID(ByteUtils.bytesToInt(contents));
            if(hnid.isBlank()) {
                //can this happen, we have the cell existence check
                throw new IllegalStateException();
            } else if(hnid.isHid()) {
                return Optional.of(prop.getType().loadVariableSize(hn.load(hnid.asHID())));
            } else {
                throw new IllegalStateException("TODO");
            }
        } else {
            row.seek(desc.getIbData());
            byte[] b = row.read(prop.getType().getSize().getSize());
            return Optional.of(
                    prop.getType()
                        .loadFixed(b)
                    );
        }
    }
    
    public String debugString() {
        StringBuilder sb = new StringBuilder();
        sb.append("hn:" +  hn.debugString() + "\n");
        sb.append("tcInfo:" + tcInfo + "\n");
        sb.append("RowIndex:" + rowIndex + "\n");
        for(int rowId : rowIndex.getRowIds()) {
            int index = rowIndex.getRowIndex(rowId);
            String contents = getRowData(index).debugString();
            sb.append("rowId:" + rowId + " rowIdHex:" + ByteUtils.intToHex(rowId) + " index:" + index + " data:" + contents + "\n");
        }
        return sb.toString();
    }
}
    
    
    
