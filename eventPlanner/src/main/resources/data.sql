INSERT INTO location ( city, country, street, house_number) VALUES
                                                                   ( 'New York', 'USA', '5th Avenue', '101'),
                                                                   ( 'London', 'UK', 'Baker Street', '221B'),
                                                                   ( 'Berlin', 'Germany', 'Unter den Linden', '50');

INSERT INTO account (email, password, role, last_password_reset_date,notifications_silenced, status, user_id) VALUES
                                                                                                                     ( 'auth@mail.com', '$2a$10$ViS.UAa9KZMu4luKceTt8OP9z6Y35SKmBlE.CxCbGEejVWuDfXpuC', 0,'2023-12-01 10:00:00', FALSE, 0, null),
                                                                                                                     ( 'organizer@mail.com', '$2a$10$ViS.UAa9KZMu4luKceTt8OP9z6Y35SKmBlE.CxCbGEejVWuDfXpuC', 1,'2023-12-01 10:00:00', TRUE, 0, null),
                                                                                                                     ( 'provider@mail.com', '$2a$10$ViS.UAa9KZMu4luKceTt8OP9z6Y35SKmBlE.CxCbGEejVWuDfXpuC', 2,'2023-12-01 10:00:00', TRUE, 0, null),
                                                                                                                     ( 'admin@mail.com', '$2a$10$ViS.UAa9KZMu4luKceTt8OP9z6Y35SKmBlE.CxCbGEejVWuDfXpuC', 3,'2023-12-01 10:00:00', TRUE, 0, null);


INSERT INTO company ( email, name, phone_number, description, location_id) VALUES
                                                                                  ( 'info@techcorp.com', 'TechCorp', '555-1234', 'Leading tech company.', 1);

INSERT INTO users ( dtype, first_name, last_name, phone_number, profile_photo, location_id, account_id, company_id) VALUES
                                                                                                                           ('Provider', 'John', 'Doe', '123-456-7890', NULL, 1, 3,1),
                                                                                                                           ('Organizer', 'Jane', 'Smith', '987-654-3210', NULL, 2, 2, NULL);


INSERT INTO offering_category (name, description, is_deleted, pending) VALUES
                                                                               ('Electronics', 'Category for electronic items.', FALSE, FALSE),
                                                                               ( 'Home Services', 'Category for home-related services.', FALSE, TRUE);

INSERT INTO event_type ( name, description, is_active) VALUES
                                                              ( 'Workshop', 'Hands-on learning sessions.', TRUE),
                                                              ( 'Conference', 'Large gatherings for presentations and discussions.', TRUE);


INSERT INTO event_stats (one_star_count, two_star_count, three_star_count, four_star_count, five_star_count, participants_count, average_rating) VALUES
                                                                                                                                     (2,1,5,2,4,14, 3.3),
                                                                                                                                     (4,2,6,1,1,14, 2.8),
                                                                                                                                     (1,2,1,0,0,4, 2.0),
                                                                                                                                     (0,0,2,3,7,12, 4.4),
                                                                                                                                     (4,2,1,7,1,15, 3.2);

INSERT INTO event (organizer_id, event_type_id, name, description, max_participants, is_open, date, is_deleted, location_id, date_created, stats_id) VALUES
                                                                                                                                                   ( 2, 2, 'Tech Workshop', 'Learn about the latest tech trends.', 50, TRUE, '2024-01-15 12:00', FALSE, 1, '2024-03-10', 1),
                                                                                                                                                   ( 2, 1, 'Business Conference', 'Annual business networking event.', 200, FALSE, '2024-03-10 15:00', FALSE, 2, '2024-03-03',2),
                                                                                                                                                   ( 2, 1, 'Music Festival', 'Enjoy live performances from top artists.', 500, TRUE, '2024-07-20 09:00', FALSE, 3, '2024-05-01',3),
                                                                                                                                                   ( 2, 2, 'Charity Gala', 'Fundraising dinner for a noble cause.', 150, FALSE, '2024-10-15 08:30', FALSE, 2, '2024-08-12',4),
                                                                                                                                                   ( 2, 1, 'Art Exhibition', 'Showcasing modern art pieces.', 100, TRUE, '2024-09-01 18:00', FALSE, 1, '2024-07-15',5);
INSERT INTO agenda_item (name, description, location, start_time, end_time, is_deleted) VALUES
                                                                                                ( 'Opening Session', 'Kick-off of the event.', 'Main Hall', '09:00:00', '10:00:00', FALSE),
                                                                                                ( 'Keynote Speech', 'Special guest speaker.', 'Conference Room A', '10:30:00', '11:30:00', FALSE);

INSERT INTO budget_item (amount, purchase_date, is_deleted, category_id, offering_id) VALUES
                                                                                              ( 500.00, '2023-11-01 14:00:00', FALSE, 1, NULL),
                                                                                              ( 200.00, '2023-11-02 15:00:00', FALSE, 2, NULL);

INSERT INTO comment (content, status, commenter_id) VALUES
                                                            ( 'Great event!', 1, 1),
                                                            ( 'Needs better organization.', 0, 2);


INSERT INTO message (content, timestamp, sender_id, receiver_id, is_read) VALUES
                                                                                  ( 'Hello, when is the event?', '2023-12-05 09:00:00', 1, 2, FALSE),
                                                                                  ( 'Can you provide more details?', '2023-12-06 10:00:00', 2, 1, TRUE);

INSERT INTO notification (is_read, content) VALUES
                                                    ( FALSE, 'Your account has been updated.'),
                                                    ( TRUE, 'Event registration confirmed.');


INSERT INTO product_details (name, description, price, discount, is_visible, is_available, timestamp) VALUES
                                                                                                              ( 'Wedding Decoration Set', 'Complete set of decorations for weddings, including table centerpieces and backdrops.', 200.00, 10.0, TRUE, FALSE, '2023-10-20 15:00:00'),
                                                                                                              ( 'Conference Projector', 'High-resolution projector for conferences and business meetings.', 1200.00, 10.0, TRUE, TRUE, '2023-10-25 10:30:00'),
                                                                                                              ( 'Sound System', 'Professional sound system for parties and celebrations.', 850.00, 50.0, TRUE, TRUE, '2023-10-28 11:45:00'),
                                                                                                              ( 'Banquet Chairs', 'Comfortable and elegant chairs for formal events.', 25.00, 20.0, TRUE, FALSE, '2023-11-01 09:15:00'),
                                                                                                              ( 'Table Linens', 'High-quality linens for event tables.', 15.00, 10.0, TRUE, TRUE, '2023-11-05 14:00:00'),
                                                                                                              ( 'Stage Lighting Kit', 'Adjustable lighting kit for stage events.', 500.00, 0.0, TRUE, TRUE, '2023-11-08 16:30:00'),
                                                                                                              ( 'Wedding Cake Topper', 'Customizable cake toppers for weddings.', 50.00, 50.0, TRUE, TRUE, '2023-11-12 13:20:00'),
                                                                                                              ( 'Portable Dance Floor', 'Easy-to-assemble dance floor for events.', 700.00, 70.0, TRUE, FALSE, '2023-11-15 17:00:00'),
                                                                                                              ( 'Photo Booth', 'Self-service photo booth with props for celebrations.', 1500.00, 10.0, TRUE, TRUE, '2023-11-18 12:00:00'),
                                                                                                              ( 'Funeral Memorial Kit', 'Set of candles, floral arrangements, and remembrance cards.', 120.00, 10.0, TRUE, TRUE, '2023-11-20 18:45:00');


INSERT INTO service_details (name, description, specification, price, discount, fixed_time, max_duration, min_duration, cancellation_period, reservation_period, is_visible, is_available, auto_confirm, timestamp) VALUES
                                                                                                                                                                                                                            ( 'Wedding Photography', 'Professional photography for weddings.', 'Includes pre-event shoot, event coverage, and photo album.', 1200.00, 100.0, TRUE, 8, 6, 48, 72, TRUE, TRUE, TRUE, '2023-10-30 09:00:00'),
                                                                                                                                                                                                                            ( 'Event Catering', 'Complete catering service for events.', 'Includes setup, serving, and cleanup for up to 200 guests.', 5000.00, 500.0, TRUE, 10, 4, 72, 96, TRUE, TRUE, FALSE, '2023-11-03 11:30:00'),
                                                                                                                                                                                                                            ( 'DJ Service', 'Professional DJ for weddings and parties.', 'Includes sound system and personalized playlist.', 700.00, 50.0, TRUE, 6, 3, 24, 48, TRUE, TRUE, TRUE, '2023-11-06 19:00:00'),
                                                                                                                                                                                                                            ( 'Conference Setup', 'Venue setup for business conferences.', 'Includes table arrangements, projector setup, and refreshments.', 1500.00, 150.0, TRUE, 5, 3, 48, 72, TRUE, TRUE, FALSE, '2023-11-10 08:00:00'),
                                                                                                                                                                                                                            ( 'Event Security', 'Security personnel for large events.', 'Up to 5 guards equipped for crowd management.', 1000.00, 100.0, TRUE, 8, 4, 24, 48, TRUE, TRUE, TRUE, '2023-11-14 20:15:00'),
                                                                                                                                                                                                                            ( 'Floral Arrangement', 'Custom floral arrangements for weddings or funerals.', 'Includes bouquets, table arrangements, and venue decor.', 300.00, 25.0, TRUE, 6, 2, 24, 48, TRUE, TRUE, FALSE, '2023-11-16 10:10:00'),
                                                                                                                                                                                                                            ( 'Master of Ceremonies', 'Experienced MC for formal events.', 'Includes script preparation and event hosting.', 400.00, 40.0, TRUE, 6, 3, 24, 48, TRUE, TRUE, TRUE, '2023-11-19 14:25:00'),
                                                                                                                                                                                                                            ( 'Funeral Planning', 'Comprehensive funeral arrangement services.', 'Includes venue setup, catering, and floral decor.', 2000.00, 200.0, TRUE, 10, 6, 72, 96, TRUE, TRUE, FALSE, '2023-11-22 09:50:00'),
                                                                                                                                                                                                                            ( 'Party Balloon Setup', 'Balloon decorations for birthdays and celebrations.', 'Includes customized balloon arches and centerpieces.', 250.00, 20.0, TRUE, 4, 2, 24, 48, TRUE, TRUE, TRUE, '2023-11-25 13:40:00'),
                                                                                                                                                                                                                            ( 'Event Clean-Up', 'Post-event cleanup service.', 'Includes garbage disposal and venue tidying.', 500.00, 50.0, TRUE, 5, 2, 24, 48, TRUE, TRUE, FALSE, '2023-11-28 18:00:00');

INSERT INTO rating (rater_id, score) VALUES
                                          (1,1),
                                          (1,2),
                                          (2,3),
                                          (1,5),
                                          (1,2),
                                          (1,3),
                                          (2,1),
                                          (1,4),
                                          (1,1),
                                          (1,2),
                                          (2,3),
                                          (1,5),
                                          (1,2),
                                          (1,3),
                                          (2,1),
                                          (1,4);




INSERT INTO reservation (id, start_time, end_time, status, event_id, service_id) VALUES
                                                                                     (1, '2024-01-15 09:00:00', '2024-01-15 17:00:00', 1, 1, NULL),
                                                                                     (2, '2024-03-10 08:00:00', '2024-03-10 20:00:00', 0, 2, 1);

INSERT INTO message (content, timestamp, sender_id, receiver_id, is_read) VALUES
                                                                                  ( 'Can you confirm my registration?', '2023-12-07 11:00:00', 3, 1, FALSE),
                                                                                  ( 'Thank you for the details!', '2023-12-07 12:30:00', 2, 3, TRUE),
                                                                                  ( 'What are the event topics?', '2023-12-08 10:15:00', 3, 2, FALSE),
                                                                                  ( 'Topics will be shared soon.', '2023-12-08 11:45:00', 2, 3, TRUE),
                                                                                  ( 'Do you have a schedule?', '2023-12-09 14:20:00', 1, 2, FALSE),
                                                                                  ( 'Yes, I will send it shortly.', '2023-12-09 15:00:00', 2, 1, TRUE),
                                                                                  ('Will there be lunch provided?', '2023-12-10 09:10:00', 3, 2, FALSE),
                                                                                  ( 'Lunch is included in the ticket.', '2023-12-10 10:30:00', 2, 1, TRUE),
                                                                                  ( 'Can I get a refund?', '2023-12-11 16:40:00', 1, 2, FALSE),
                                                                                  ( 'Refunds are processed within 7 days.', '2023-12-11 17:50:00', 2, 1, TRUE);
INSERT INTO offerings (dtype, category_id, provider_id, current_product_details_id, is_deleted, pending) VALUES
                                                                                       ('Product',1 ,1, 1, FALSE, FALSE),
                                                                                       ('Product',2 ,1,  2, FALSE, FALSE),
                                                                                       ('Product',1 ,1,  3, FALSE, FALSE),
                                                                                       ('Product',1 ,1,  4, FALSE, FALSE),
                                                                                       ('Product',1 ,1,  5, FALSE, FALSE),
                                                                                       ( 'Product',2 ,1,  6, FALSE, FALSE),
                                                                                       ( 'Product',1 ,1,  7, FALSE, FALSE),
                                                                                       ( 'Product',1 ,1,  8, FALSE, FALSE),
                                                                                       ( 'Product',2 ,1,  9, FALSE, FALSE),
                                                                                       ( 'Product',1 ,1,  10, FALSE, FALSE);
INSERT INTO offerings (dtype, category_id, provider_id, current_service_details_id, is_deleted, pending) VALUES
                                                                                       ( 'Service',1 ,1,  1, FALSE, FALSE),
                                                                                       ( 'Service',2 ,1,  2, FALSE, FALSE),
                                                                                       ( 'Service',1 ,1,  3, FALSE, FALSE),
                                                                                       ( 'Service',1 ,1,  4, FALSE, FALSE),
                                                                                       ( 'Service',2 ,1,  5, FALSE, FALSE),
                                                                                       ( 'Service',1 ,1,  6, FALSE, FALSE),
                                                                                       ( 'Service',1 ,1,  7, FALSE, FALSE),
                                                                                       ( 'Service',1 ,1,  8, FALSE, FALSE),
                                                                                       ( 'Service',1 ,1,  9, FALSE, FALSE),
                                                                                       ( 'Service',2 ,1,  10, FALSE, FALSE);
INSERT INTO offerings_ratings (offering_id, ratings_id) VALUES
                                                           (1, 3),
                                                           (2, 5),
                                                           (3, 7),
                                                           (4, 9),
                                                           (5, 11),
                                                           (6, 13),
                                                           (7, 15),
                                                           (8, 1),
                                                           (9, 4),
                                                           (10, 6),
                                                           (11, 8),
                                                           (12, 10),
                                                           (13, 12),
                                                           (14, 14),
                                                           (15, 16),
                                                           (16, 2),
                                                           (17, 3),
                                                           (18, 5),
                                                           (19, 7),
                                                           (20, 9),
                                                           (1, 11),
                                                           (2, 13),
                                                           (3, 15),
                                                           (4, 1),
                                                           (5, 4),
                                                           (6, 6),
                                                           (7, 8),
                                                           (8, 10),
                                                           (9, 12),
                                                           (10, 14);


INSERT INTO rating (score, rater_id) VALUES
                                             ( 5, 1),
                                             ( 3, 2);

INSERT INTO reservation (start_time, end_time, status, event_id, service_id) VALUES
                                                                                     ( '2024-01-15 09:00:00', '2024-01-15 17:00:00', 1, 1, NULL),
                                                                                     ( '2024-03-10 08:00:00', '2024-03-10 20:00:00', 0, 2, 1);
