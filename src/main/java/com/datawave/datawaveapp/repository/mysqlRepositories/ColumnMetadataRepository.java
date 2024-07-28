package com.datawave.datawaveapp.repository.mysqlRepositories;

import com.datawave.datawaveapp.model.entity.ColumnMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColumnMetadataRepository extends JpaRepository<ColumnMetadataEntity, Long> {

}
