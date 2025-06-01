import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUser PreferenceArray;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUser Neighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUser BasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.util.*;

public class RecommendationSystem {
    private static Map<Long, PreferenceArray> userData = new HashMap<>();
    private static UserBasedRecommender recommender;

    public static void main(String[] args) {
        initializeSampleData();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("1. Get Recommendations for a User");
            System.out.println("2. Add User Preferences");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter User ID for recommendations: ");
                    long userId = scanner.nextLong();
                    getRecommendations(userId);
                    break;
                case 2:
                    addUser Preferences(scanner);
                    break;
                case 3:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
    }

    private static void initializeSampleData() {
        // Sample data initialization (same as before)
        // User 1 preferences
        PreferenceArray user1Prefs = new GenericUser PreferenceArray(3);
        user1Prefs.setUser ID(0, 1L);
        user1Prefs.setItemID(0, 101L);
        user1Prefs.setValue(0, 5.0f);
        user1Prefs.setUser ID(1, 1L);
        user1Prefs.setItemID(1, 102L);
        user1Prefs.setValue(1, 3.0f);
        user1Prefs.setUser ID(2, 1L);
        user1Prefs.setItemID(2, 103L);
        user1Prefs.setValue(2, 2.5f);
        userData.put(1L, user1Prefs);

        // User 2 preferences
        PreferenceArray user2Prefs = new GenericUser PreferenceArray(3);
        user2Prefs.setUser ID(0, 2L);
        user2Prefs.setItemID(0, 101L);
        user2Prefs.setValue(0, 2.0f);
        user2Prefs.setUser ID(1, 2L);
        user2Prefs.setItemID(1, 102L);
        user2Prefs.setValue(1, 2.5f);
        user2Prefs.setUser ID(2, 2L);
        user2Prefs.setItemID(2, 104L);
        user2Prefs.setValue(2, 5.0f);
        userData.put(2L, user2Prefs);

        // User 3 preferences
        PreferenceArray user3Prefs = new GenericUser PreferenceArray(4);
        user3Prefs.setUser ID(0, 3L);
        user3Prefs.setItemID(0, 101L);
        user3Prefs.setValue(0, 2.5f);
        user3Prefs.setUser ID(1, 3L);
        user3Prefs.setItemID(1, 103L);
        user3Prefs.setValue(1, 4.0f);
        user3Prefs.setUser ID(2, 3L);
        user3Prefs.setItemID(2, 104L);
        user3Prefs.setValue(2, 4.5f);
        user3Prefs.setUser ID(3, 3L);
        user3Prefs.setItemID(3, 105L);
        user3Prefs.setValue(3, 5.0f);
        userData.put(3L, user3Prefs);

        // User 4 preferences
        PreferenceArray user4Prefs = new GenericUser PreferenceArray(2);
        user4Prefs.setUser ID(0, 4L);
        user4Prefs.setItemID(0, 102L);
        user4Prefs.setValue(0, 4.0f);
        user4Prefs.setUser ID(1, 4L);
        user4Prefs.setItemID(1, 105L);
        user4Prefs.setValue(1, 3.0f);
        userData.put(4L, user4Prefs);

        // Create DataModel from user preferences
        DataModel model = new GenericDataModel(userData);

        // User similarity based on Pearson correlation
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);

        // Define user neighborhood with threshold similarity
        UserNeighborhood neighborhood = new ThresholdUser Neighborhood(0.1, similarity, model);

        // Create recommender
        recommender = new GenericUser BasedRecommender(model, neighborhood, similarity);
    }

    private static void getRecommendations(long userId) {
        try {
            List<RecommendedItem> recommendations = recommender.recommend(userId, 3);
            System.out.println("Recommendations for User " + userId + ":");
            if (recommendations.isEmpty()) {
                System.out.println("No recommendations available.");
            } else {
                for (RecommendedItem recommendation : recommendations) {
                    System.out.printf("Product ID: %d, Predicted Preference: %.2f%n",
                            recommendation.getItemID(), recommendation.getValue());
                }
            }
        } catch (TasteException e) {
            System.err.println("Error in recommendation engine: " + e.getMessage());
        }
    }

    private static void addUser Preferences(Scanner scanner) {
        System.out.print("Enter User ID: ");
        long userId = scanner.nextLong();
        System.out.print("Enter number of preferences to add: ");
        int numPrefs = scanner.nextInt();
        PreferenceArray prefs = new GenericUser PreferenceArray(numPrefs);

        for (int i = 0; i < numPrefs; i++) {
            System.out.print("Enter Product ID for preference " + (i + 1) + ": ");
            long itemId = scanner.nextLong();
            System.out.print("Enter rating (0-5) for Product ID " + itemId + ": ");
            float rating = scanner.nextFloat();
            prefs.setUser ID(i, userId);
            prefs.setItemID(i, itemId);
            prefs.setValue(i, rating);
        }

        userData.put(userId, prefs);
        System.out.println("Preferences added for User " + userId);
    }
}
