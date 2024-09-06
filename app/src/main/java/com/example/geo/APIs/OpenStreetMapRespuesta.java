package com.example.geo.APIs;

import com.example.geo.Models.CallePorCoordenadas;

public class OpenStreetMapRespuesta {
    private CallePorCoordenadas address;

    public CallePorCoordenadas getAddress() {
        return address;
    }

    public void setAddress(CallePorCoordenadas address) {
        this.address = address;
    }
}
