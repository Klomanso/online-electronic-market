package org.project.onlineelectronicmarket.service;

import org.project.onlineelectronicmarket.model.Status;

import java.util.Optional;

public interface StatusService {

        Status save(Status status);

        void delete(Status status);

        Optional<Status> update(Status newStatus);

        Optional<Status> findByName(String name);

        Status processing();

        Status complete();

        Status delivered();
}
