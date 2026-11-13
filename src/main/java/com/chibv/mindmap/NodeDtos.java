package com.chibv.mindmap;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Dữ liệu trao đổi với Angular. Tên field khớp với interface MmNode ở frontend. */
public final class NodeDtos {

    private NodeDtos() {
    }

    public record NodeResponse(Long id, Long parentId, String title, String content, double posX, double posY) {

        static NodeResponse from(MmNode n) {
            return new NodeResponse(n.getId(), n.getParentId(), n.getTitle(), n.getContent(), n.getPosX(), n.getPosY());
        }
    }

    public record CreateRequest(
            Long parentId,
            @NotBlank @Size(max = 200) String title,
            String content,
            double posX,
            double posY) {
    }

    public record UpdateRequest(
            @NotBlank @Size(max = 200) String title,
            String content) {
    }

    public record MoveRequest(@NotNull Double x, @NotNull Double y) {
    }
}
