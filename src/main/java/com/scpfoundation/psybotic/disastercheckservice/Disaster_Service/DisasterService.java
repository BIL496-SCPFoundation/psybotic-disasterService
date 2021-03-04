package com.scpfoundation.psybotic.disastercheckservice.Disaster_Service;

import com.scpfoundation.psybotic.disastercheckservice.Models.Disaster;

public interface DisasterService {
    public Disaster  saveDisaster(Disaster ds);
    public Disaster  findDisasterId(String id);
}
