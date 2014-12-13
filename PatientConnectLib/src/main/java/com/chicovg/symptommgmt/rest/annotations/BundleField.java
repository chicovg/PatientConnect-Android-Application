package com.chicovg.symptommgmt.rest.annotations;

/**
 * This annotation marks a field for inclusion
 *  when converting the object to a 'Bundle'
 *  the type field specifies what type the field
 *  will be handled as;
 * Created by victorguthrie on 11/22/14.
 */
public @interface BundleField {
    BundleFieldType type();
}
