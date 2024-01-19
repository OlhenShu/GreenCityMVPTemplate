package greencity.service;

public interface NewsSubscriberService {
    /**
     * Saves a new news subscriber with the provided email address.
     *
     * @param email The email address of the news subscriber to be saved.
     */
    void save(String email);
}
