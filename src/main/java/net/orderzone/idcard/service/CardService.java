package net.orderzone.idcard.service;

import net.orderzone.idcard.dto.CardRequestDTO;

import java.util.List;

public interface CardService {

    String generatePreviewHtml(CardRequestDTO request);

    byte[] generatePDF(Long profileId);

    byte[] generateBatchZip(List<Long> profileIds, Long templateId);
}
