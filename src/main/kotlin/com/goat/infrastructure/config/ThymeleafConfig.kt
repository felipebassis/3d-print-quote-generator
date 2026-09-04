package com.goat.infrastructure.config

import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.thymeleaf.TemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.FileTemplateResolver

@ApplicationScoped
internal class ThymeleafConfig {

    @Produces
    @ApplicationScoped
    fun templateEngine(
        @ConfigProperty(name = "file-system.template-path") templatePath: String,
    ): TemplateEngine {
        val fileTemplateResolver = FileTemplateResolver()

        fileTemplateResolver.templateMode = TemplateMode.HTML
        fileTemplateResolver.prefix = templatePath
        fileTemplateResolver.suffix = ".html"
        fileTemplateResolver.characterEncoding = "UTF-8"
        fileTemplateResolver.isCacheable = true


        return TemplateEngine().apply {
            setTemplateResolver(fileTemplateResolver)
        }
    }
}