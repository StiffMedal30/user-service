package za.co.user.service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.user.service.records.InviteRequestRecord;
import za.co.user.service.service.InvitationService;

import java.security.Principal;

@RestController
@RequestMapping("/api/collaborator")
public class CollaboratorController {

    private final InvitationService invitationService;

    public CollaboratorController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @PostMapping("/invite")
    public ResponseEntity<?> inviteCollaborator(@RequestBody InviteRequestRecord inviteRequest, Principal principal) {
        invitationService.sendInvitation(inviteRequest, principal);
        return ResponseEntity.ok("Invitation sent successfully.");
    }
}
