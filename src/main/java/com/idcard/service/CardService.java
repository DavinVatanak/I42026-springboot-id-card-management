package com.idcard.service;

import com.idcard.dto.CardRequestDTO;

import java.util.List;

/**
 * Service interface for ID Card generation (preview, PDF, batch).
 */
public interface CardService {

    /**
     * Generates a preview HTML string for the given profile + template.
     *
     * @param request contains profileId and templateId
     * @return rendered HTML string
     */
    String generatePreviewHtml(CardRequestDTO request);

    /**
     * Generates a downloadable PDF for a single profile.
     *
     * @param profileId the profile to generate for
     * @return PDF bytes
     */
    byte[] generatePDF(Long profileId);

    /**
     * Generates multiple PDF cards and packages them into a ZIP archive.
     *
     * @param profileIds list of profile IDs
     * @param templateId the template to use
     * @return ZIP archive bytes
     */
    byte[] generateBatchZip(List<Long> profileIds, Long templateId);
}
