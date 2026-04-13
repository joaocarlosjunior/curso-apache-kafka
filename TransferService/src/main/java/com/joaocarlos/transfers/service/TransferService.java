package com.joaocarlos.transfers.service;

import com.joaocarlos.transfers.model.TransferRestModel;

public interface TransferService {
    public boolean transfer(TransferRestModel productPaymentRestModel);
}
