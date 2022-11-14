package pro.sky.telegrambot.controller;

import org.springframework.web.bind.annotation.*;
import pro.sky.telegrambot.model.Interaction;
import pro.sky.telegrambot.service.InteractionService;

import java.util.Collection;

@RestController
@RequestMapping("/request")
public class InteractionController {

    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @PostMapping
    public Interaction postInteraction(@RequestBody Interaction interaction) {
        return interactionService.postInteraction(interaction);
    }

    @GetMapping
    public String getRequest(@RequestParam(value = "request") String request) {
        return interactionService.getResponseByRequest(request);
    }

    @GetMapping("/all")
    public Collection<Interaction> getAllResponse() {
        return interactionService.getAllPossibleInteractions();
    }

    @PutMapping
    public Interaction updateInteraction(@RequestBody Interaction interaction) {
        return interactionService.updateInteraction(interaction);
    }

    @DeleteMapping
    public Interaction deleteInteraction(Interaction interaction) {
        return interactionService.deleteInteraction(interaction);
    }
}
