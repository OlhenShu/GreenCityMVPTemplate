package greencity.validator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(SpringExtension.class)
public class ImageValidatorTest {

    @InjectMocks
    private ImageValidator imageValidator;

    @Test
    public void isValidImageJpegTest() {
        MultipartFile multipartFile = new MockMultipartFile("Image", "Image", "image/jpeg", new byte[0]);

        assertTrue(imageValidator.isValid(multipartFile, null));
    }


    @Test
    public void isValidImagePngTest() {
        MultipartFile multipartFile = new MockMultipartFile("Image", "Image", "image/png", new byte[0]);

        assertTrue(imageValidator.isValid(multipartFile, null));
    }

    @Test
    public void isValidImageJpgTest() {
        MultipartFile multipartFile = new MockMultipartFile("Image", "Image", "image/jpg", new byte[0]);

        assertTrue(imageValidator.isValid(multipartFile, null));
    }

    @Test
    public void isValidImageNullTest() {
        MultipartFile multipartFile = null;

        assertTrue(imageValidator.isValid(multipartFile, null));
    }

    @Test
    public void isValidImageWrongTypeTest() {
        MultipartFile multipartFile = new MockMultipartFile("Image", "Image", "image/jpgd", new byte[0]);

        assertFalse(imageValidator.isValid(multipartFile, null));
    }

    @Test
    public void initializeTest() {
        assertDoesNotThrow(() -> imageValidator.initialize(null));
    }
}
