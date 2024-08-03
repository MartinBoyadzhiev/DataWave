package com.datawave.datawaveapp.service;

import com.datawave.datawaveapp.exceptions.ResourceNotFoundException;
import com.datawave.datawaveapp.model.dto.InsertDataDTO;

import java.io.IOException;

public interface DataService {
    void insertData(InsertDataDTO insertDataDTO) throws IOException, ResourceNotFoundException;
}
