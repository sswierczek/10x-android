# Firebase Realtime Database Schema for MovieMind

## 1. Database Structure

### Users
```
/users/{userId}
  - username: string
  - email: string
  - createdAt: timestamp
  - lastLoginAt: timestamp
```

### Movie Lists
```
/lists/{listId}
  - name: string
  - userId: string (reference to /users/{userId})
  - createdAt: timestamp
  - updatedAt: timestamp
```

### Movies in Lists
```
/lists/{listId}/movies/{movieId}
  - movieId: string (TMDB ID)
  - addedAt: timestamp
  - order: number
```

### Watch History
```
/watchHistory/{userId}/{movieId}
  - movieId: string (TMDB ID)
  - watchedAt: timestamp
  - rating: number (1-5)
  - review: string
```

## 2. Relationships

- **Users to Lists**: One-to-many relationship
  - Each user can have multiple lists
  - Each list belongs to exactly one user

- **Lists to Movies**: One-to-many relationship
  - Each list can contain multiple movies
  - Each movie in a list is associated with exactly one list

- **Users to Watch History**: One-to-many relationship
  - Each user can have multiple entries in watch history
  - Each watch history entry belongs to exactly one user

## 3. Indexes

Firebase Realtime Database automatically indexes all fields, but we'll define explicit indexes for common queries:

```
{
  "rules": {
    "lists": {
      ".indexOn": ["userId", "createdAt"]
    },
    "watchHistory": {
      ".indexOn": ["userId", "watchedAt"]
    }
  }
}
```

## 4. Security Rules

```
{
  "rules": {
    "users": {
      "$userId": {
        ".read": "$userId === auth.uid",
        ".write": "$userId === auth.uid"
      }
    },
    "lists": {
      ".read": "auth != null",
      ".write": "auth != null",
      "$listId": {
        ".read": "auth != null",
        ".write": "data.child('userId').val() === auth.uid",
        ".validate": "newData.hasChild('userId') && newData.child('userId').val() === auth.uid"
      }
    },
    "lists": {
      "$listId": {
        "movies": {
          ".read": "auth != null",
          ".write": "root.child('lists').child($listId).child('userId').val() === auth.uid"
        }
      }
    },
    "watchHistory": {
      "$userId": {
        ".read": "$userId === auth.uid",
        ".write": "$userId === auth.uid",
        "$movieId": {
          ".validate": "newData.hasChild('rating') && newData.child('rating').isNumber() && newData.child('rating').val() >= 1 && newData.child('rating').val() <= 5"
        }
      }
    }
  }
}
```

## 5. Design Decisions and Notes

1. **Denormalization for Performance**: 
   - We're storing movie IDs directly in lists and watch history rather than creating separate movie entities, as all movie data will be fetched from TMDB API.
   - This approach reduces database reads and simplifies the data structure.

2. **Timestamp Fields**:
   - All timestamp fields use Firebase's server timestamp feature to ensure consistency across devices.
   - This helps with sorting and tracking when records were created or updated.

3. **Security Rules**:
   - Users can only read and write their own data.
   - Lists can be read by any authenticated user but can only be written by the owner.
   - Watch history entries can only be read and written by the owner.
   - Validation ensures that ratings are between 1 and 5.

4. **Data Structure for Lists**:
   - Lists are stored at the root level for easier querying.
   - Movies within lists are stored as children of the list, with the movie ID as the key.
   - This structure allows for efficient querying of all movies in a list.

5. **Watch History Structure**:
   - Watch history is organized by user ID and then by movie ID.
   - This structure allows for efficient querying of a user's watch history.
   - Each entry contains the movie ID, rating, review, and timestamp.

6. **No Separate Reviews Table**:
   - Reviews are stored directly in the watch history entries.
   - This simplifies the data structure and ensures that reviews are always associated with a watched movie.

7. **Order Field for Movies in Lists**:
   - The order field allows for maintaining the order of movies in a list.
   - This is important since we're displaying movies in the order they were added.

8. **No Edit Functionality**:
   - As per requirements, users can only delete their lists and reviews, not edit them.
   - This is enforced through the application logic rather than database rules.

9. **No Social Features**:
   - The database structure doesn't include any social features, as per requirements.
   - Each user's data is isolated and not shared with other users.

10. **Scalability Considerations**:
    - The structure is designed to scale well with a growing number of users and lists.
    - Indexes are defined for common queries to ensure good performance.
    - The denormalized structure reduces the number of reads needed to fetch data. 