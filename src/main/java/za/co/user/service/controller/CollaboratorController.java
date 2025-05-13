package za.co.user.service.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.user.service.records.InviteRequestRecord;
import za.co.user.service.service.InvitationService;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/collaborator")
@AllArgsConstructor
public class CollaboratorController {

    private final InvitationService invitationService;

    @PostMapping("/invite")
    public ResponseEntity<Map<String, String>> inviteCollaborator(@RequestBody InviteRequestRecord inviteRequest, Principal principal) {
        try {
            invitationService.sendInvitation(inviteRequest, principal);
            return ResponseEntity.ok(Map.of("message", "Invitation sent successfully."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
