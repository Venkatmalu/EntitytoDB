package com.springboot.entity;

import java.util.Map;

import javax.persistence.ElementCollection;

import com.poiji.annotation.ExcelRow;
import com.poiji.annotation.ExcelUnknownCells;

public class ExcelData {

	
	@ExcelRow
	private long rowIndex;
	
	@ElementCollection
	@ExcelUnknownCells
    private Map<?, ?> data;
	
	public long getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(long rowIndex) {
		this.rowIndex = rowIndex;
	}

	public Map<?, ?> getUnknownCells() {
		return data;
	}

	public void setUnknownCells(Map<?, ?> unknownCells) {
		this.data = unknownCells;
	}

}