package ru.vood.test

import ru.vood.advanсed.deprecated.DeprecatedWithRemoval

/** asdASD
 * */
//@Deprecated("asd")
@DeprecatedWithRemoval(
    message = "asdasd",
    removalDate = "2000-12-12",
    deletedInVersion = "1.1.2",
    )
final class Annotated {

//    @Deprecated("asd")
    final fun qwerty(){}
}