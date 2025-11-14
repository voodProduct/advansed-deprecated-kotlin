package ru.vood.advanced.deprecated.ksp.util

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class VersionComparatorTest : BehaviorSpec({
    given("success parsing") {


        withData<Pair<String, VersionComparator.Version>>(
            { (input, _) -> "версия '$input' должна парситься корректно" },
            "1.2.3" to VersionComparator.Version(1, 2, 3),
            "1.2" to VersionComparator.Version(1, 2, null),
            "12" to VersionComparator.Version(12, null, null),
            "1.2.3-beta" to VersionComparator.Version(1, 2, 3, "-beta"),
            "1.2.3beta" to VersionComparator.Version(1, 2, 3, "beta"),
            "1.2.3-rc1" to VersionComparator.Version(1, 2, 3, "-rc1"),
            "0.0.1" to VersionComparator.Version(0, 0, 1),
            "10.20.30" to VersionComparator.Version(10, 20, 30),
            "1.2.3-alpha+build" to VersionComparator.Version(1, 2, 3, "-alpha+build")
        ) { (input, expected) ->
            VersionComparator.parseVersion(input) shouldBe expected
        }
    }
    given("error parsing") {
        withData<String>(
            { input -> "версия '$input' должна бросать исключение" },
//            "1.2",
//            "1.2.",
            ".2.3",
            "a.b.c",
            "1.2.3-",
            "",
//            "1.2.3.4"
        ) { invalidVersion ->
            runCatching {
                VersionComparator.parseVersion(invalidVersion)
            }.isFailure shouldBe true
        }
    }

    given("VersionComparator") {
        withData<Triple<String, String, Boolean>>(
            { (current, target, expected) ->
                "current: $current, target: $target, expected: $expected"
            },
            Triple("2.0.0", "1.0.0", true),   // major больше
            Triple("1.1.0", "1.0.0", true),   // minor больше
            Triple("1.0.1", "1.0.0", true),   // patch больше
            Triple("1.0.0", "1.0.0", true),   // равны
            Triple("1.0.0", "2.0.0", false),  // major меньше
            Triple("1.0.0", "1.1.0", false),  // minor меньше
            Triple("1.0.0", "1.0.1", false),  // patch меньше
            Triple("1.0.0-beta", "1.0.0", false),  // суффикс не влияет
            Triple("1.0.0", "1.0.0-beta", true),  // суффикс влияет
            Triple("1.1.1", "1.1.0", true),   // patch больше при равных major/minor
            Triple("2.1.0", "2.0.9", true),   // minor больше при равном major
            Triple("3.0.0", "2.9.9", true)    // major больше
        ) { (current, target, expected) ->
            VersionComparator.isCurrentVersionGreaterOrEqual(current, target) shouldBe expected
        }
    }
})