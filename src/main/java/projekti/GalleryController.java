
package projekti;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class GalleryController {
    
    @Autowired
    private ImageObjectRepository imageRepository;  
    
    @Autowired
    private GalleryRepository galleryRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private AccountService accountService;

    @GetMapping("/gallery/{galleryId}")
    public String getGallery(Model model, @PathVariable String galleryId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account account = accountService.findAuthenticatedAccount(auth.getName());
        
        if (account != null) {
            model.addAttribute("profileId", account.getProfile().getId());
        } else {
            model.addAttribute("profileId", null);
        }

        model.addAttribute("isOwner", accountService.isOwnerOfGallery(account, galleryId));
        Gallery gallery = galleryRepository.findByProfileId(galleryId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kuva-galleriaa ei löytynyt"));  
        model.addAttribute("isFriend", account.getFriends().contains(gallery.getOwner()));
        model.addAttribute("avatarId", gallery.getOwner().getAvatarId());
        model.addAttribute("gallery", gallery);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("datetime").descending());
        List<ImageObject> imageobjects = imageRepository.findByProfileId(galleryId, pageable);
        model.addAttribute("images", imageobjects);
        List<List<Comment>> comments = new ArrayList<>();
        for (int i = 0; i < imageobjects.size(); i++) {
            comments.add(commentRepository.findByParentId(imageobjects.get(i).getId(), pageable));
        }
        model.addAttribute("comments", comments);
        return "gallery";
    }
    
    @Secured("ROLE_POSTER")
    @Transactional
    @PostMapping("/gallery/{galleryId}")
    public String addImage(@RequestParam("file") MultipartFile file, @RequestParam("description") String description, @PathVariable String galleryId) throws IOException {
        Gallery gallery = galleryRepository.findByProfileId(galleryId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kuva-galleriaa ei löytynyt"));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account account = accountService.findAuthenticatedAccount(auth.getName());

        if (!account.getGallery().getProfileId().equals(galleryId)) {
            return "redirect:/gallery/" + galleryId;
        }
        
        if (gallery.getImages().size() >= 10) {
            return "redirect:/gallery/" + galleryId;
        }
        
        ByteArrayInputStream bytes = new ByteArrayInputStream(file.getBytes());
        
        if (ImageIO.read(bytes) == null) {
            return "redirect:/gallery/" + galleryId;
        }
        System.out.println("here4");
        ImageObject imageObject = new ImageObject();
        imageObject.setContent(file.getBytes());
        imageObject.setDescription(description);
        imageObject.setDatetime(LocalDateTime.now());
        imageObject.setProfileId(galleryId);
        imageRepository.save(imageObject);
        gallery.getImages().add(imageObject);
        
        galleryRepository.save(gallery);
        return "redirect:/gallery/" + galleryId;
    }
    
    @Secured("ROLE_POSTER")
    @Transactional
    @PostMapping("/gallery/delete/{imageId}")
    public String deleteImage(@PathVariable Long imageId) {
        ImageObject image = imageRepository.findById(imageId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kuvaa, jota olit kommentoimassa, ei löytynyt"));
        Gallery gallery = galleryRepository.findByProfileId(image.getProfileId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kuva-galleriaa ei löytynyt"));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account account = accountService.findAuthenticatedAccount(auth.getName());

        if (!account.getGallery().getProfileId().equals(gallery.getProfileId())) {
            return "redirect:/gallery/" + gallery.getProfileId();
        }
        
        if (account.getAvatarId() == image.getId()) {
            account.setAvatarId(null);
            accountService.save(account);
        }
        gallery.getImages().remove(image);
        galleryRepository.save(gallery);
        imageRepository.delete(image);
        return "redirect:/gallery/" + gallery.getProfileId();
    }
    
    @Secured("ROLE_POSTER")
    @Transactional
    @PostMapping("/gallery/comment/{imageId}")
    public String addComment(@RequestParam String content, @PathVariable Long imageId) {        
        ImageObject image = imageRepository.findById(imageId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kuvaa, jota olit kommentoimassa, ei löytynyt"));
        Gallery gallery = galleryRepository.findByProfileId(image.getProfileId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kuva-galleriaa ei löytynyt"));
        if (content != null && !content.isEmpty()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Account sender = accountService.findAuthenticatedAccount(auth.getName());
            Comment comment = new Comment(image.getId(), sender, content, LocalDateTime.now(), new ArrayList<>(), 0);
            commentRepository.save(comment);
            image.getComments().add(comment);
            imageRepository.save(image);
            gallery.getCommentIds().add(comment.getId());
            galleryRepository.save(gallery);
        }
        return "redirect:/gallery/" + gallery.getProfileId();
    }
 
    @GetMapping(path = "/gallery/image/{id}")
    @ResponseBody
    public byte[] getContent(@PathVariable Long id) {
        return imageRepository.getOne(id).getContent();
    }
    
    @Secured("ROLE_POSTER")
    @Transactional
    @PostMapping("/gallery/avatar/{imageId}")
    public String setProfilePicture(@PathVariable Long imageId) {
        ImageObject image = imageRepository.findById(imageId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kuvaa, jota olit asettamassa profiilikuvaksi, ei löytynyt"));
        Gallery gallery = galleryRepository.findByProfileId(image.getProfileId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kuva-galleriaa ei löytynyt"));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account account = accountService.findAuthenticatedAccount(auth.getName());

        if (!account.getGallery().getProfileId().equals(gallery.getProfileId())) {
            return "redirect:/gallery/" + gallery.getProfileId();
        }
        
        account.setAvatarId(image.getId());
        accountService.save(account);
        return "redirect:/gallery/" + gallery.getProfileId();
    }
    
    @Secured("ROLE_POSTER")
    @Transactional
    @PostMapping("/gallery/image/like/{imageId}")
    public String likeImage(@PathVariable Long imageId) {
        ImageObject image = imageRepository.findById(imageId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kuvaa, jota olit tykkäämässä, ei löytynyt"));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account account = accountService.findAuthenticatedAccount(auth.getName());   
        if (image.getLikingAccountIds().contains(account.getId())) {
            image.getLikingAccountIds().remove(account.getId());
            image.setNumberOfLikes(image.getNumberOfLikes() - 1);
        } else {
            image.getLikingAccountIds().add(account.getId());
            image.setNumberOfLikes(image.getNumberOfLikes() + 1);
        }
        return "redirect:/gallery/" + image.getProfileId();
    }
    
    @Secured("ROLE_POSTER")
    @Transactional
    @PostMapping("/gallery/{galleryId}/comment/like/{commentId}")    
    public String likeComment(@PathVariable String galleryId, @PathVariable Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kommenttia, jota olit tykkäämässä, ei löytynyt"));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account account = accountService.findAuthenticatedAccount(auth.getName());
        
        if (comment.getLikingAccountIds().contains(account.getId())) {
            comment.getLikingAccountIds().remove(account.getId());
            comment.setNumberOfLikes(comment.getNumberOfLikes() - 1);
        } else {
            comment.getLikingAccountIds().add(account.getId());
            comment.setNumberOfLikes(comment.getNumberOfLikes() + 1);
        }
        commentRepository.save(comment);
        return "redirect:/gallery/" + galleryId;
    }
}
