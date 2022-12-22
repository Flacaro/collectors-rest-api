package org.univaq.collectors.controllers.requests.payload;

import javax.validation.constraints.NotNull;

public class FavouriteDiskPayload {
    @NotNull
    private Long diskId;

    public Long getDiskId(){
        return diskId;
    }

    public void setDiskId(Long diskId){
        this.diskId = diskId;
    }
}
