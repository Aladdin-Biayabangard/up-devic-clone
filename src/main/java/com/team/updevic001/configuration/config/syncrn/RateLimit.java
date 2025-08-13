package com.team.updevic001.configuration.config.syncrn;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)  // Bu annotasiya yalnız metodlar üçün tətbiq olunur
@Retention(RetentionPolicy.RUNTIME) // Runtime-da mövcud olacaq
public @interface RateLimit {
}
