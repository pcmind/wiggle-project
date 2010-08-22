/* This file is part of the db4o object database http://www.db4o.com

Copyright (C) 2004 - 2009  Versant Corporation http://www.versant.com

db4o is free software; you can redistribute it and/or modify it under
the terms of version 3 of the GNU General Public License as published
by the Free Software Foundation.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program.  If not, see http://www.gnu.org/licenses/. */
package com.db4o.internal.transactionlog;

import com.db4o.foundation.io.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;
import com.db4o.io.*;

/**
 * @exclude
 */
public class FileBasedTransactionLogHandler extends TransactionLogHandler {
	
	static final int LOCK_INT = Integer.MAX_VALUE - 1;
	
	private Bin _lockFile;
	
	private Bin _logFile;
	
	private final String _fileName;
	
	
	public FileBasedTransactionLogHandler(LocalTransaction trans, String fileName) {
		_fileName = fileName;
	}

	public static String logFileName(String fileName) {
		return fileName + ".log";
	}

	public static String lockFileName(String fileName) {
		return fileName + ".lock";
	}

	private Bin openBin(LocalTransaction trans, String fileName) {
		
		return new FileStorage().open(new BinConfiguration(fileName, trans.config().lockFile(), 0, false));
	}
	
	public boolean checkForInterruptedTransaction(LocalTransaction trans, ByteArrayBuffer reader) {
		reader.incrementOffset(Const4.INT_LENGTH * 2);
		if(!File4.exists(lockFileName(_fileName))){
			return false;
		}
		return lockFileSignalsInterruptedTransaction(trans);
	}

	private boolean lockFileSignalsInterruptedTransaction(LocalTransaction trans) {
		openLockFile(trans);
		ByteArrayBuffer buffer = newLockFileBuffer();
		read(_lockFile, buffer);
		for (int i = 0; i < 2; i++) {
			int checkInt = buffer.readInt();
			if(checkInt != LOCK_INT){
				closeLockFile();
				return false;
			}
		}
		closeLockFile();
		return true;
	}

	public void close() {
		if(!logsOpened()){
			return;
		}
		closeLockFile();
		closeLogFile();
		deleteLockFile();
		deleteLogFile();
	}

	private void closeLockFile() {
		syncAndClose(_lockFile);
	}

	private void syncAndClose(Bin bin) {
		try {
			bin.sync();
		}
		finally {
			bin.close();
		}
	}

	private void closeLogFile() {
		syncAndClose(_logFile);
	}

	private void deleteLockFile() {
		File4.delete(lockFileName(_fileName));
	}

	private void deleteLogFile() {
		File4.delete(logFileName(_fileName));
	}

	@Override
	public Slot allocateSlot(LocalTransaction trans, boolean append) {
		// do nothing
		return null;
	}

	@Override
	public void applySlotChanges(LocalTransaction trans, Slot reservedSlot) {
		int slotChangeCount = countSlotChanges(trans);
		if(slotChangeCount < 1){
			return;
		}
		
		flushDatabaseFile(trans);
		
		ensureLogAndLock(trans);
		int length = transactionLogSlotLength(trans);
		ByteArrayBuffer logBuffer = new ByteArrayBuffer(length);
		logBuffer.writeInt(length);
		logBuffer.writeInt(slotChangeCount);
	
	    appendSlotChanges(trans, logBuffer);
	    write(_logFile, logBuffer);
	    _logFile.sync();
	    
	    writeToLockFile(LOCK_INT);

	    if (trans.writeSlots()) {
	    	flushDatabaseFile(trans);
	    }
	    writeToLockFile(0);
	}
	
	private void writeToLockFile(int lockSignal) {
	    ByteArrayBuffer lockBuffer = newLockFileBuffer();
		lockBuffer.writeInt(lockSignal);
	    lockBuffer.writeInt(lockSignal);
	    write(_lockFile, lockBuffer);
	    _lockFile.sync();
	}

	private ByteArrayBuffer newLockFileBuffer() {
		return new ByteArrayBuffer(lockFileBufferLength());
	}

	private int lockFileBufferLength() {
		return Const4.LONG_LENGTH * 2;
	}

	private void ensureLogAndLock(LocalTransaction trans) {
		if(trans.config().isReadOnly()){
			return;
		}
		if(logsOpened()){
			return;
		}
		openLockFile(trans);
		openLogFile(trans);
	}

	private void openLogFile(LocalTransaction trans) {
		_logFile = openBin(trans, logFileName(_fileName));
	}

	private void openLockFile(LocalTransaction trans) {
		_lockFile = openBin(trans, lockFileName(_fileName));
	}

	private boolean logsOpened() {
		return _lockFile != null;
	}

	@Override
	public void completeInterruptedTransaction(LocalTransaction trans) {
		ByteArrayBuffer buffer = new ByteArrayBuffer(Const4.INT_LENGTH);
		openLogFile(trans);
		read(_logFile, buffer);
		int length = buffer.readInt();
		if(length > 0){
			buffer = new ByteArrayBuffer(length);
			read(_logFile, buffer);
			buffer.incrementOffset(Const4.INT_LENGTH);
			trans.readSlotChanges(buffer);
            if(trans.writeSlots()){
                flushDatabaseFile(trans);
            }
            deleteLockFile();
            trans.freeSlotChanges(false);
		}else{
			deleteLockFile();
		}
		closeLogFile();
		deleteLogFile();
	}

	private void read(Bin storage, ByteArrayBuffer buffer) {
		storage.read(0, buffer._buffer, buffer.length());
	}
	
	private void write(Bin storage, ByteArrayBuffer buffer) {
		storage.write(0, buffer._buffer, buffer.length());
	}
	
}
