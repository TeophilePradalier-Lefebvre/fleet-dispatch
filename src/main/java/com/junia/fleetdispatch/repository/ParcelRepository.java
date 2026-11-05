package com.junia.fleetdispatch.repository;

import com.junia.fleetdispatch.model.Parcel;

import java.util.List;

public interface ParcelRepository {

    List<Parcel> findAll();
}