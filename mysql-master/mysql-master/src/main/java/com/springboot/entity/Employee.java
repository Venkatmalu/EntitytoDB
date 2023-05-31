package com.springboot.entity;

import java.util.Map;

import javax.persistence.ElementCollection;

import com.poiji.annotation.ExcelRow;
import com.poiji.annotation.ExcelUnknownCells;

public class Employee {

	@ExcelRow
	private int rowIndex;

	@ElementCollection
	@ExcelUnknownCells
    public Map<String, String> unknownCells;
	
	public Map<String, String> getUnknownCells() {
		return unknownCells;
	}

	public void setUnknownCells(Map<String, String> unknownCells) {
		this.unknownCells = unknownCells;
	}

	@Override
	public String toString() {
		return "Employee [rowIndex=" + rowIndex + ", unknownCells=" + unknownCells + "]";
	}




}
