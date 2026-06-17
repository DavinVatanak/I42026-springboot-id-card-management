package com.idcard.service;

import com.idcard.model.Profile;
import com.idcard.model.Template;

/**
 * Service interface for PDF generation using iText.
 */
public interface PDFService {

    /**
     * Generates a PDF ID card for the given profile.
     *
     * @param profile  the profile entity
     * @param template the template entity (for styling hints)
     * @return PDF bytes
     */
    byte[] generateIDCardPDF(Profile profile, Template template);
}
