package uk.ac.ebi.subs.ena.security;

import org.springframework.security.access.prepost.PostAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by neilg on 05/05/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@PostAuthorize("returnObject == null or hasAnyRole(@roleLookup.adminRole(),returnObject.team.name)")
public @interface PostAuthoriseSubmissionAccountId {}

