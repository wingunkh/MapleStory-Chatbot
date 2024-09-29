package com.maple;

import com.maple.domain.*;
import com.maple.repository.EventRepository;
import com.maple.repository.NoticeRepository;
import com.maple.repository.ShopRepository;
import com.maple.repository.UpdateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
public class RepositoryIntegrationTest {
    private final NoticeRepository noticeRepository;
    private final UpdateRepository updateRepository;
    private final EventRepository eventRepository;
    private final ShopRepository shopRepository;

    @Autowired
    public RepositoryIntegrationTest (NoticeRepository noticeRepository, UpdateRepository updateRepository, EventRepository eventRepository, ShopRepository shopRepository) {
        this.noticeRepository = noticeRepository;
        this.updateRepository = updateRepository;
        this.eventRepository = eventRepository;
        this.shopRepository = shopRepository;
    }

    private <T extends Information> void verifyFindAll(List<T> result) {
        assertThat(result)
                .isNotEmpty()
                .hasSizeLessThanOrEqualTo(10);
    }

    @Test
    public void findAllNotice() {
        // given

        // when
        List<Notice> result = noticeRepository.findAll();

        // then
        verifyFindAll(result);
    }

    @Test
    public void findAllClientUpdate() {
        // given

        // when
        List<ClientUpdate> result = updateRepository.findAll();

        // then
        verifyFindAll(result);
    }

    @Test
    public void findAllEvent() {
        // given

        // when
        List<Event> result = eventRepository.findAll();

        // then
        verifyFindAll(result);
    }

    @Test
    public void findAllShop() {
        // given

        // when
        List<Shop> result = shopRepository.findAll();

        // then
        verifyFindAll(result);
    }
}
