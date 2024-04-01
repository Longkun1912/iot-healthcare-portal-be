-- Drop the old devices table
DROP TABLE IF EXISTS devices;

-- Create the new devices table
CREATE TABLE devices (
    id uuid PRIMARY KEY,
    name text,
    label text,
    is_gateway boolean,
    is_active boolean,
    type text,
    device_profile_name text,
    additional_info text,
    created_time timestamp,
    "device_owner" uuid,
    foreign key ("device_owner") references users(id)
);