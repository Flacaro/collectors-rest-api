package org.univaq.collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.type.EntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SerializeWithView {
    @Autowired
    private ObjectMapper mapper;

    private final Map<EntityView, Map<ViewType, Class<?>>> views = Map.of(
            EntityView.COLLECTOR, Map.of(
                    ViewType.PRIVATE, UserView.Private.class,
                    ViewType.PUBLIC, UserView.Public.class
            ),
            EntityView.COLLECTION, Map.of(
                    ViewType.PRIVATE, UserView.Private.class,
                    ViewType.PUBLIC, UserView.Public.class
            ),
            EntityView.DISK, Map.of(
                    ViewType.PRIVATE, UserView.Private.class,
                    ViewType.PUBLIC, UserView.Public.class
            ),
            EntityView.TRACK, Map.of(
                    ViewType.PRIVATE, UserView.Private.class,
                    ViewType.PUBLIC, UserView.Public.class
            )
    );


    public String serialize(EntityView targetViewEntity, ViewType targetView, Object object) throws JsonProcessingException {
            return mapper
                    .writerWithView(views.get(targetViewEntity).get(targetView))
                    .writeValueAsString(object);
    }



    public enum EntityView {
        COLLECTOR,
        COLLECTION,
        DISK,
        TRACK
    }

    public enum ViewType {
        PRIVATE,
        PUBLIC
    }

}
