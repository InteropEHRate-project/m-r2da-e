package eu.interopehrate.mr2de.api;

/*
 *      Author: Engineering S.p.A. (www.eng.it)
 *     Project: InteropEHRate - www.interopehrate.eu
 *
 * Description: Enumeration representing the format of medical data requested with MR2D.
 */
@Deprecated
public enum ResponseFormat {

    /*
    Data are represented as FHIR Resources, using only semantic codes defined by the InteropEHRate
    profile and obtained from the conversion of the codes(if any) in the original source data or by
    means of a semi automatic information extraction process. Semantic codes obtained by information
     extraction will allow the fhirClient to show labels and portion of the original natural language
     content in the language of the user (both HCP and Citizen)
    STRUCTURED_CONVERTED,
     */

    /*
    Data are represented as FHIR Resources, using same semantic codes (if any) as the original
    source data (no conversion of semantic codes is perfomed)
     */

    STRUCTURED_UNCONVERTED,

    /*
    Data are represented in a human readable document format (e.g. PDF). No information extraction
    or conversion of semantic code is performed. The exact content of the returned document is not
    defined by the MR2D protocol.
     */
    UNSTRUCTURED,

    /*
    Data are returned both in the same format returned by UNSTRUCTURED value and by
    STRUCTURED_CONVERTED (if the server is able to convert), or STRUCTURED_UNCONVERTED
    (if the server is not able to convert but is able to transform the content in FHIR format).
    If the server is not able to perform any data transformations, only UNSTRUCTURED values
    are returned.
     */

    ALL;
}
