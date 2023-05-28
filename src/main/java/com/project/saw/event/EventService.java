package com.project.saw.event;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EventService {

        private final EventRepository eventRepository;

        public EventService(EventRepository eventRepository) {
                this.eventRepository = eventRepository;
        }

        public List<EventEntity> getAll(){
                return eventRepository.findAll();
        }
}
