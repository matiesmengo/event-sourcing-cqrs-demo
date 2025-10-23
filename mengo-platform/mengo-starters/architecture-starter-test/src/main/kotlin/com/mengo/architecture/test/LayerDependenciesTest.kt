package com.mengo.architecture.test

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder
import org.springframework.boot.autoconfigure.SpringBootApplication

@Suppress("unused")
@AnalyzeClasses(
    packages = ["com.mengo"],
    importOptions = [ImportOption.DoNotIncludeTests::class],
)
internal class LayerDependenciesTest {
    @ArchTest
    fun domainLayerShouldBePure(classes: JavaClasses) {
        val basePackages = detectSpringBootApplications()

        layeredArchitecture()
            .consideringOnlyDependenciesInAnyPackage(
                basePackages.joinToString(",") { "$it.." },
            ).layer("Domain")
            .definedBy("..domain..")
            .layer("Application")
            .definedBy("..application..")
            .layer("Infrastructure")
            .definedBy("..infrastructure..")
            .whereLayer("Domain")
            .mayOnlyAccessLayers("Domain")
            .whereLayer("Application")
            .mayOnlyAccessLayers("Application", "Domain")
            .whereLayer("Infrastructure")
            .mayOnlyAccessLayers("Infrastructure", "Application", "Domain")
            .check(classes)
    }

    fun detectSpringBootApplications(vararg roots: String = arrayOf("com.mengo")): List<String> {
        val reflections =
            Reflections(ConfigurationBuilder().forPackages(*roots).addScanners(Scanners.TypesAnnotated))

        return reflections
            .getTypesAnnotatedWith(SpringBootApplication::class.java)
            .map { it.`package`.name }
            .distinct()
    }
}
