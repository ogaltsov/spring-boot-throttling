package com.github.ogaltsov.amzscouttesttask.component.quoting;

/**
 * Ключ квотирования - по нему счетаем квоты на вызов
 *
 * @param ip с которого вызывается ресурс
 * @param resource тип запрашиваемого ресурса
 */
public record QuoteKey(
   String ip,
   String resource
) {}
