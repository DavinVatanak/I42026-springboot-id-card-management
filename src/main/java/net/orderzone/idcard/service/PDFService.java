package net.orderzone.idcard.service;

import net.orderzone.idcard.model.Profile;
import net.orderzone.idcard.model.Template;

public interface PDFService {

    /**
     * Generates a PDF ID card using the profile's own template colours.
     *
     * @param profile  the fully-loaded profile entity
     * @param template the template to use for colour theming
     * @return raw PDF bytes
     */
    byte[] generateIDCardPDF(Profile profile, Template template);
}
