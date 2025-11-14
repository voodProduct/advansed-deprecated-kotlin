package ru.vood.test

import ru.vood.advanсed.deprecated.DeprecatedWithRemoval

@DeprecatedWithRemoval(
    message = "asdasd",
//    removalDate = "2000-12-12",
//    deletedInVersion = "0.1.2",
    )
final class Annotated {

    @DeprecatedWithRemoval(
        message = "asdasd",
//    removalDate = "2000-12-12",
//    deletedInVersion = "0.1.2",
    )
    final fun qwerty(){}
}