package project.atch.domain.user.dto;

import project.atch.domain.notice.entity.Notice;

public record NoticeResponse(String title, String content, boolean isItem) {
    public static NoticeResponse from(Notice notice) {
        return new NoticeResponse(notice.getTitle(), notice.getContent(), notice.isItem());
    }
}
