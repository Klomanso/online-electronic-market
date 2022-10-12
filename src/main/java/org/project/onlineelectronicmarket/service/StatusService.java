package org.project.onlineelectronicmarket.service;

import java.util.Optional;

import org.project.onlineelectronicmarket.model.Status;
import org.project.onlineelectronicmarket.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

@Service
public class StatusService {

        private final StatusRepository statusRepository;

        @Autowired
        public StatusService(StatusRepository statusRepository) {
                this.statusRepository = statusRepository;
        }

        public Status save(Status status) {
                return statusRepository.save(status);
        }

        public void delete(Status status) {
                statusRepository.delete(status);
        }

        @Modifying
        public Optional<Status> update(Status newStatus) {
                Optional<Status> oldStatus = findByName(newStatus.getName());

                if (oldStatus.isPresent()) {
                        Status savedStatus = save(newStatus);
                        return Optional.of(savedStatus);
                } else {
                        return oldStatus;
                }
        }

        public Optional<Status> findByName(String name) {
                return statusRepository.findByName(name);
        }

        public Status processing() {
                return findByName("processing").get();
        }

        public Status complete() {
                return findByName("complete").get();
        }

        public Status delivered() {
                return findByName("delivered").get();
        }
}
