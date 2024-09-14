package project.atch.domain.user.dto;

import project.atch.domain.user.entity.Notice;

public record NoticeResponse(String title, String content) {
    public static NoticeResponse from(Notice notice) {
        return new NoticeResponse(notice.getTitle(), notice.getContent());
    }
}
