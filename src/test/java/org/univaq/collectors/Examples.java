package org.univaq.collectors;

import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.CollectorEntity;

import java.time.LocalDate;

public class Examples {

    public static CollectorEntity collectorExample() {
        return new CollectorEntity(
                null,
                "Mario",
                "Rossi",
                LocalDate.of(1990, 1, 1),
                "mario1",
                "mario@rossi.com",
                "secret",
                null
        );
    }

    public static CollectorEntity collectorExample2() {
        return new CollectorEntity(
                null,
                "Rosa",
                "Bianchi",
                LocalDate.of(1990, 1, 1),
                "Rosa",
                "rosa@bianchi.com",
                "secret",
                null
        );

    }

    public static CollectorEntity collectorExample3() {
        return new CollectorEntity(
                null,
                "Daniele",
                "Neri",
                LocalDate.of(1990, 1, 1),
                "Daniele",
                "daniele@neri.com",
                "secret",
                null
        );

    }


    public static CollectionEntity collectionExample() {
        return new CollectionEntity(
                null,
                "collezione 1",
                "buono",
                true
        );
    }

    public static CollectionEntity collectionExample2() {
        return new CollectionEntity(
                null,
                "collezione 2",
                "ottimo",
                false
        );
    }

}
