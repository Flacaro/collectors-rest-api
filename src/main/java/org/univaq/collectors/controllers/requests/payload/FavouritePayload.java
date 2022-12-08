package org.univaq.collectors.controllers.requests.payload;

import javax.validation.constraints.NotNull;

public class FavouritePayload {
    @NotNull
    private Long collectionId;

    public Long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }
}
