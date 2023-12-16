package greencity.mapping;

import greencity.exception.exceptions.NotSavedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class MultipartBase64ImageMapperTest {

    @InjectMocks
    MultipartBase64ImageMapper mapper;

    @Test
    void convert() throws IOException {

        String base64Image = "data:image/jpeg;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA\n" +
                             "AAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO\n" +
                             "9TXL0Y4OHwAAAABJRU5ErkJggg==";

        MultipartFile multipartFile = mapper.convert(base64Image);

        assertNotNull(multipartFile);
        assertEquals("image/jpeg", multipartFile.getContentType());
        assertNotNull(multipartFile.getBytes());
    }

    @Test
    void convertWithIOException_ShouldThrowNotSavedException() {

        String invalidImage = "data:image/jpeg;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA\\n\" +\n" +
                              "                             \"AAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO\\n\" +\n" +
                              "                             \"9TXL0Y4OHwAAAABJRU5ErkJggg==";

        assertThrows(NotSavedException.class, () -> mapper.convert(invalidImage));
    }
}