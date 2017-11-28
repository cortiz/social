package org.craftercms.social.controllers.rest.v3.comments;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.collections.IterableUtils;
import org.craftercms.profile.api.Profile;
import org.craftercms.social.controllers.rest.v3.comments.exceptions.UGCNotFound;
import org.craftercms.social.domain.social.Flag;
import org.craftercms.social.domain.social.ModerationStatus;
import org.craftercms.social.domain.social.SocialUgc;
import org.craftercms.social.exceptions.IllegalUgcException;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.exceptions.UGCException;
import org.craftercms.social.security.SocialSecurityUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

/**
 */
@Controller
public class CommentsController<T extends SocialUgc> extends AbstractCommentsController {

    private Logger log = LoggerFactory.getLogger(CommentsController.class);

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Creates a new comment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public T create(@ApiParam(value = "Body of the comment, some Html/scripts tags will be stripped") @RequestParam()
                        final String body, @ApiParam(name = "thread",
        value = "Id of the thread to attach to comment") @RequestParam(required = true) final String thread,
                    @ApiParam(value = "Id of the parent for the new comment", name = "parentId") @RequestParam
                        (required = false, defaultValue = "") final String parent, @ApiParam(value = "Should This " +
        "comment be posted as anonymous ", name = "anonymous") @RequestParam(required = false, defaultValue = "false",
        value = "anonymous") final boolean anonymous, @ApiParam(value = "Subject of the comment to be " + "created",
        name = "subject") @RequestParam(required = false, defaultValue = "", value = "subject") final String subject,
                    @ApiParam(value = "Json String representing any extra attributes of the comment to create",
        name = "attributes") @RequestParam(required = false,
        defaultValue = "{}") final String attributes, MultipartFile attachment) throws SocialException,
        MissingServletRequestParameterException, IOException {
        Map<String, Object> attributesMap = null;

        if (!StringUtils.isBlank(attributes)) {
            attributesMap = parseAttributes(attributes);
        }
        T newUgc = (T)ugcService.create(context(), parent, thread, body, subject, attributesMap, checkAnonymous
            (anonymous));

        if (attachment != null) {
            ugcService.addAttachment(newUgc.getId().toString(), context(), attachment.getInputStream(), attachment
                .getOriginalFilename(), getContentType(attachment.getOriginalFilename()));
        }
        return newUgc;
    }



    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "Updates the given comment", notes = "Like Create some HTML/scripts tags will be stripped")
    @ResponseBody
    public T update( @PathVariable("id") final String id,  @RequestParam() final String body,  @RequestParam(required = false,
        defaultValue = "{}") final String attributes) throws SocialException,
        MissingServletRequestParameterException, UGCNotFound {
        Map<String, Object> attributesMap = null;
        if (!StringUtils.isBlank(attributes)) {
            attributesMap = parseAttributes(attributes);
        }
        return (T)ugcService.update(id, body, "", context(), attributesMap);
    }

    @RequestMapping(value = "{id}/update", method = RequestMethod.POST)
    @ApiOperation(value = "Updates the given comment", notes = "Like Create some HTML/scripts tags will be stripped")
    @ResponseBody
    public T updatePost( @PathVariable("id") final String id,  @RequestParam() final String body,  @RequestParam(required = false,
        defaultValue = "{}") final String attributes) throws SocialException,
        MissingServletRequestParameterException, UGCNotFound {
        return this.update(id, body, attributes);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Deletes the comment", notes = "Like Create some HTML/scripts tags will be stripped, " +
        "Also all children will be deleted")
    @ResponseBody
    public boolean delete( @PathVariable("id") final String id)
        throws SocialException {
        ugcService.deleteUgc(id, context());
        return true;
    }

    @RequestMapping(value = "{id}/delete", method = RequestMethod.POST)
    @ApiOperation(value = "Deletes the comment", notes = "Like Create some HTML/scripts tags will be stripped, " +
        "Also All children will be deleted")
    @ResponseBody
    public boolean deletePost( @PathVariable("id") final String id)
        throws SocialException {
        return this.delete(id);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    @ApiOperation(value = "Gets the comment")
    @ResponseBody
    public T read( @PathVariable("id") final String id) throws
        SocialException {
        return (T)ugcService.read(id, context());
    }


    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ApiOperation(value = "Search the comments")
    @ResponseBody
    public Iterable<T> read(
                            @ApiParam(value = "Search String can be a valid mongodb query except when defining contextId "
                                + "or using the $where operator ")
                            @RequestParam(required = true) final String search,
                            @RequestParam(required = true) final String sortBy,
                            @RequestParam(required = true)
                            int start,
                            @RequestParam(required = true)
                            int limit)
        throws  SocialException {
        return ugcService.search(context(),search,sortBy,start,limit);
    }



    @RequestMapping(value = "{id}/attributes", method = {RequestMethod.POST, RequestMethod.PUT}, consumes =
        {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @ResponseBody
    @ApiOperation(value = "Adds or updates the given attributes with the new value " + "if attribute does not " +
        "exist it will be created with Json expected to be the POST body",
        notes = "This operation expects any " +
            "type of valid JSON" +
            " " +
            "object. Notice that there is a current limitation and all attributes will be converted into a 'String'" +
            " there for all its non array-maps. This is valid for numbers, booleans and dates. Keep this in mind when" +
            " " +
            "doing the search")
    public boolean addAttributes(@ApiParam(value = "Id of the UGC") @NotBlank @PathVariable(value = "id") final
                                     String id, @ApiParam(value = "Json of the attributes to be updated or created" +
        ". All values are " + "saved as string (booleans,numbers,dates)") @RequestParam final Map<String, Object>
        attributes) throws SocialException, UGCNotFound {
        log.debug("Request for deleting form  UGC {} attributes {}", id, attributes);
        attributes.remove("context");
        ugcService.setAttributes(id, context(), attributes);
        return true;//Always true unless exception.
    }


    @RequestMapping(value = "{id}/attributes", method = RequestMethod.DELETE)
    @ResponseBody
    @ApiOperation(value = "Deletes all the attributes from the given UGC", notes = "All attributes must be in dot " +
        "notation where nested values should be in its full path, to remove multiple attributes send them separated " +
        "by a ',' ")
    public boolean removeAttributes(@ApiParam(value = "Id of the comment", name = "id") @PathVariable(value = "id")
                                        final String id, @ApiParam(name = "attributes", value = "List of ',' " +
        "separated attributes name to delete. Use dot " + "notation to delete nested attributes.") @RequestParam
    final String attributes) throws SocialException {
        log.debug("Request for deleting form  UGC {} attributes {}", id, attributes);
        ugcService.deleteAttribute(id, attributes.split(","), context());
        return true;//Always true unless exception.
    }

    @RequestMapping(value = "{id}/attributes/delete", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "Deletes all the attributes from the given UGC", notes = "All attributes must be in dot " +
        "notation where nested values should be in its full path, to remove multiple attributes send them separated " +
        "by a ',' ")
    public boolean removeAttributesPost(@ApiParam(value = "Id of the comment", name = "id") @PathVariable(value = "id")
                                    final String id, @ApiParam(name = "attributes", value = "List of ',' " +
        "separated attributes name to delete. use dot " + "notation to delete nested attributes.") @RequestParam
                                    final String attributes) throws SocialException {
       return this.removeAttributes(id, attributes);
    }

    @RequestMapping(value = "{id}/flags", method = RequestMethod.POST)
    @ResponseBody
    public T flagUgc(@ApiParam(value = "Comment Id") @PathVariable(value = "id") final String id, @ApiParam(value =
        "Reason why the comment has been flagged") @RequestParam final String reason) throws SocialException {
        return (T)socialServices.flag(id, context(), reason, userId());
    }


    @RequestMapping(value = "{id}/flags", method = RequestMethod.GET)
    @ResponseBody
    public Iterable<Flag> flagUgc( @PathVariable(value = "id") final String id) throws
        SocialException {
        T ugc = (T)ugcService.read(id, context());
        if (ugc == null) {
            throw new IllegalUgcException("Given UGC does not exist for context");
        }
        return ugc.getFlags();
    }

    @RequestMapping(value = "{id}/flags/{flagId}", method = {RequestMethod.POST, RequestMethod.DELETE})
    @ResponseBody
    public boolean unflagUgc( @PathVariable(value = "id") final String id,  @PathVariable(value = "flagId") final String flagId) throws SocialException {
        return socialServices.unFlag(id, flagId, userId(), context());
    }

    @RequestMapping(value = "{id}/moderate", method = {RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody

    public T moderate( @PathVariable final String id,  @RequestParam final ModerationStatus status) throws
        SocialException {

        return (T)socialServices.moderate(id, status, userId(), context());
    }



    @RequestMapping(value = "moderation/{status}", method = RequestMethod.GET)
    @ResponseBody

    public Iterable<T> byStatus(@PathVariable("status") final ModerationStatus status, @RequestParam
    (defaultValue = "", required = false) final String thread, @RequestParam(required = false, defaultValue =
        "0") final int pageNumber, @RequestParam(required = false, defaultValue = ThreadsController.MAX_INT) final
    int pageSize, @RequestParam(required = false) final List<String> sortBy, @RequestParam(required = false) final
    List<SocialSortOrder> sortOrder) throws UGCException {
        int start = 0;
        if (pageNumber > 0 && pageSize > 0) {
            start = ThreadsController.getStart(pageNumber, pageSize);
        }

        return IterableUtils.toList(socialServices.findByModerationStatus(status, thread, context(), start, pageSize,
            ThreadsController.getSortOrder(sortBy, sortOrder)));
    }


    @RequestMapping(value = "flagged", method = RequestMethod.GET)
    @ResponseBody

    public Iterable<T> flagged(@RequestParam(required = false, defaultValue = "0") final int pageNumber,
                               @RequestParam(required = false, defaultValue = ThreadsController.MAX_INT) final int
                                   pageSize, @RequestParam(required = false) final List<String> sortBy, @RequestParam
                                       (required = false) final List<SocialSortOrder> sortOrder) throws UGCException {
        int start = 0;
        if (pageNumber > 0 && pageSize > 0) {
            start = ThreadsController.getStart(pageNumber, pageSize);
        }

        return IterableUtils.toList(socialServices.findAllFlaggedUgs(context(), start, pageSize, ThreadsController
            .getSortOrder(sortBy, sortOrder)));
    }

    @RequestMapping(value = "flagged/count", method = RequestMethod.GET)
    @ResponseBody

    public long flaggedCount(@RequestParam(required = false, defaultValue = "0") final int pageNumber, @RequestParam
        (required = false, defaultValue = ThreadsController.MAX_INT) final int pageSize, @RequestParam(required =
        false) final List<String> sortBy, @RequestParam(required = false) final List<SocialSortOrder> sortOrder)
        throws UGCException {
        int start = 0;
        if (pageNumber > 0 && pageSize > 0) {
            start = ThreadsController.getStart(pageNumber, pageSize);
        }

        return socialServices.countAllFlaggedUgs(context(), start, pageSize, ThreadsController.getSortOrder(sortBy,
            sortOrder));
    }


    @RequestMapping(value = "moderation/{status}/count", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Counts all moderation comments with the given moderation status")
    public long byStatusCount(@PathVariable("status") final ModerationStatus status, @RequestParam
        (defaultValue = "", required = false) final String thread) throws UGCException {
        return socialServices.countByModerationStatus(status, thread, context());
    }


    protected boolean checkAnonymous(final boolean anonymous) {
        final Profile profile = SocialSecurityUtils.getCurrentProfile();
        Object isAlwaysAnonymous = profile.getAttribute("isAlwaysAnonymous");
        if (isAlwaysAnonymous == null) {
            return anonymous;
        } else {
            return ((Boolean)isAlwaysAnonymous).booleanValue();
        }

    }

}
