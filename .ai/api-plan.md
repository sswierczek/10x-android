# REST API Plan

## 1. Resources

### 1.1 Users
- Firebase Authentication user data
- Stored in Firebase Realtime Database under `/users/{uid}`
- Contains basic user information and preferences

### 1.2 Movie Lists
- User-created lists of movies
- Stored in Firebase Realtime Database under `/lists/{listId}`
- Can be public or private
- Contains metadata about the list

### 1.3 Movie List Entries
- Individual movies in a list
- Stored in Firebase Realtime Database under `/entries/{entryId}`
- Links to TMDB movie data
- Contains user-specific metadata (rating, notes, etc.)

## 2. Endpoints

### 2.1 Movie Search and Discovery (TMDB API Integration)

#### Search Movies
```
GET /api/movies/search
```
Query Parameters:
- `query` (required): Search query string
- `page` (optional): Page number for pagination (default: 1)
- `language` (optional): Language code (default: en-US)

Response:
```json
{
  "page": 1,
  "results": [
    {
      "id": 123,
      "title": "Movie Title",
      "overview": "Movie description",
      "poster_path": "/path/to/poster.jpg",
      "release_date": "2024-01-01"
    }
  ],
  "total_pages": 10,
  "total_results": 200
}
```

#### Get Movie Details
```
GET /api/movies/{movieId}
```
Response:
```json
{
  "id": 123,
  "title": "Movie Title",
  "overview": "Movie description",
  "poster_path": "/path/to/poster.jpg",
  "backdrop_path": "/path/to/backdrop.jpg",
  "release_date": "2024-01-01",
  "vote_average": 7.5,
  "runtime": 120,
  "genres": [
    {
      "id": 28,
      "name": "Action"
    }
  ]
}
```

#### Get Popular Movies
```
GET /api/movies/popular
```
Query Parameters:
- `page` (optional): Page number for pagination (default: 1)
- `language` (optional): Language code (default: en-US)

Response: Same as search movies response

### 2.2 Movie Lists (Firebase Database)

#### Get User Lists
```
GET /api/lists
```
Headers:
- `Authorization`: Firebase ID token

Response:
```json
{
  "lists": [
    {
      "id": "list123",
      "name": "My Favorite Movies",
      "description": "A list of my favorite movies",
      "isPublic": true,
      "movieCount": 10,
      "createdAt": "2024-04-14T12:00:00Z"
    }
  ]
}
```

#### Get List Details
```
GET /api/lists/{listId}
```
Headers:
- `Authorization`: Firebase ID token

Response:
```json
{
  "id": "list123",
  "name": "My Favorite Movies",
  "description": "A list of my favorite movies",
  "isPublic": true,
  "movieCount": 10,
  "createdAt": "2024-04-14T12:00:00Z",
  "userId": "user123"
}
```

#### Get List Movies
```
GET /api/lists/{listId}/movies
```
Headers:
- `Authorization`: Firebase ID token

Response:
```json
{
  "movies": [
    {
      "id": "entry123",
      "movieId": 123,
      "listId": "list123",
      "addedAt": "2024-04-14T12:00:00Z",
      "rating": 5,
      "notes": "Great movie!",
      "movie": {
        "id": 123,
        "title": "Movie Title",
        "poster_path": "/path/to/poster.jpg"
      }
    }
  ]
}
```

## 3. Authentication and Authorization

### 3.1 Authentication
- Uses Firebase Authentication
- All endpoints except public movie data require a valid Firebase ID token
- Token must be included in the Authorization header

### 3.2 Authorization Rules
- Users can only access their own lists and entries
- Public lists are readable by all authenticated users
- List creation and modification restricted to list owner

## 4. Validation and Business Logic

### 4.1 Data Validation
- User data must contain uid and email
- Lists must contain userId and name
- List entries must contain listId and movieId

### 4.2 Business Logic
- Movie data is fetched from TMDB API and cached in Firebase
- List entries reference TMDB movie IDs
- Public lists are visible to all authenticated users
- Private lists are only visible to the owner
- Movie search and discovery use TMDB API directly
- List operations use Firebase Realtime Database

### 4.3 Rate Limiting
- TMDB API rate limits apply to movie endpoints
- Firebase Database rules handle rate limiting for list operations

### 4.4 Error Handling
- Standard HTTP status codes
- Detailed error messages for validation failures
- Proper error handling for TMDB API failures
- Firebase Database error handling

## 5. Security Considerations

### 5.1 API Security
- All endpoints use HTTPS
- Firebase ID token validation
- Proper CORS configuration
- Input sanitization

### 5.2 Data Security
- Firebase Database rules enforce access control
- User data isolation
- Public/private list visibility control
- Secure storage of API keys

## 6. Performance Considerations

### 6.1 Caching
- TMDB movie data caching
- List data caching
- Proper cache invalidation

### 6.2 Optimization
- Pagination for list endpoints
- Efficient database queries
- Minimal data transfer
- Proper indexing in Firebase 