package com.project.saw.user;

import com.project.saw.dto.user.UserProjections;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<UserProjections, EntityModel<UserProjections>> {
    @Override
    public EntityModel<UserProjections> toModel(UserProjections user) {
        return EntityModel.of(user, linkTo(methodOn(UserController.class)).slash(user.getId()).withSelfRel());
    }
}
