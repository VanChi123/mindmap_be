package com.chibv.mindmap;

import com.chibv.mindmap.NodeDtos.CreateRequest;
import com.chibv.mindmap.NodeDtos.MoveRequest;
import com.chibv.mindmap.NodeDtos.NodeResponse;
import com.chibv.mindmap.NodeDtos.UpdateRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/nodes")
public class MmNodeController {

    private final MmNodeRepository repo;

    public MmNodeController(MmNodeRepository repo) {
        this.repo = repo;
    }

    /** Tải toàn bộ sơ đồ. */
    @GetMapping
    @Transactional(readOnly = true)
    public List<NodeResponse> list() {
        return repo.findAll().stream().map(NodeResponse::from).toList();
    }

    /** Thêm một ô (đối tượng gốc nếu parentId = null, ngược lại là mục con). */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public NodeResponse create(@Valid @RequestBody CreateRequest req) {
        if (req.parentId() != null && !repo.existsById(req.parentId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ô cha không tồn tại");
        }
        MmNode n = new MmNode();
        n.setParentId(req.parentId());
        n.setTitle(req.title().trim());
        n.setContent(req.content());
        n.setPosX(req.posX());
        n.setPosY(req.posY());
        return NodeResponse.from(repo.save(n));
    }

    /** Sửa tên và nội dung. Không đụng tới vị trí. */
    @PutMapping("/{id}")
    @Transactional
    public NodeResponse update(@PathVariable Long id, @Valid @RequestBody UpdateRequest req) {
        MmNode n = find(id);
        n.setTitle(req.title().trim());
        n.setContent(req.content());
        return NodeResponse.from(repo.save(n));
    }

    /** Lưu vị trí sau khi kéo thả. Không đụng tới nội dung. */
    @PatchMapping("/{id}/position")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void move(@PathVariable Long id, @Valid @RequestBody MoveRequest req) {
        MmNode n = find(id);
        n.setPosX(req.x());
        n.setPosY(req.y());
        repo.save(n);
    }

    /** Xoá một ô. Các ô con bị xoá theo nhờ ON DELETE CASCADE trong Oracle. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void delete(@PathVariable Long id) {
        repo.delete(find(id));
    }

    private MmNode find(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy ô " + id));
    }
}
