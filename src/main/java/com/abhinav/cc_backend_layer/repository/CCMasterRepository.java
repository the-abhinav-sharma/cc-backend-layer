package com.abhinav.cc_backend_layer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abhinav.cc_backend_layer.model.CCMaster;
import com.abhinav.cc_backend_layer.model.CCMasterKey;

public interface CCMasterRepository extends JpaRepository<CCMaster, CCMasterKey> {

}
