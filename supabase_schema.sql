-- =============================================
-- HotRoom Supabase Database Schema v2
-- Business Logic va Data Relationships
-- Supabase Dashboard → SQL Editor da ishga tushiring
-- =============================================

-- ============ PROFILES ============
CREATE TABLE IF NOT EXISTS profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    name TEXT NOT NULL DEFAULT '',
    role TEXT NOT NULL DEFAULT 'gardener',
    avatar_url TEXT,
    greenhouse_location TEXT,
    greenhouse_type TEXT,
    greenhouse_area REAL,
    notify_watering BOOLEAN NOT NULL DEFAULT TRUE,
    notify_temperature BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE OR REPLACE FUNCTION handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO profiles (id, name)
    VALUES (NEW.id, COALESCE(NEW.raw_user_meta_data->>'name', ''));
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
    AFTER INSERT ON auth.users
    FOR EACH ROW EXECUTE FUNCTION handle_new_user();

ALTER TABLE profiles ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Users can view own profile" ON profiles;
DROP POLICY IF EXISTS "Users can update own profile" ON profiles;
CREATE POLICY "Users can view own profile" ON profiles FOR SELECT USING (auth.uid() = id);
CREATE POLICY "Users can update own profile" ON profiles FOR UPDATE USING (auth.uid() = id);

-- ============ PLANTS ============
CREATE TABLE IF NOT EXISTS plants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    scientific_name TEXT,
    category TEXT NOT NULL DEFAULT 'sabzavot',
    image_url TEXT,
    health_status INT NOT NULL DEFAULT 100,
    planted_date DATE,
    last_watered TIMESTAMPTZ,
    has_sensor BOOLEAN NOT NULL DEFAULT FALSE,
    watering_interval_hours INT NOT NULL DEFAULT 24,
    zone TEXT,
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

ALTER TABLE plants ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Users can view own plants" ON plants;
DROP POLICY IF EXISTS "Users can insert own plants" ON plants;
DROP POLICY IF EXISTS "Users can update own plants" ON plants;
DROP POLICY IF EXISTS "Users can delete own plants" ON plants;
CREATE POLICY "Users can view own plants" ON plants FOR SELECT USING (auth.uid() = user_id);
CREATE POLICY "Users can insert own plants" ON plants FOR INSERT WITH CHECK (auth.uid() = user_id);
CREATE POLICY "Users can update own plants" ON plants FOR UPDATE USING (auth.uid() = user_id);
CREATE POLICY "Users can delete own plants" ON plants FOR DELETE USING (auth.uid() = user_id);

-- ============ CARE TASKS ============
CREATE TABLE IF NOT EXISTS care_tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    plant_id UUID REFERENCES plants(id) ON DELETE SET NULL,
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    description TEXT,
    task_type TEXT NOT NULL DEFAULT 'watering',
    scheduled_date DATE NOT NULL,
    scheduled_time TIME,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

ALTER TABLE care_tasks ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Users can view own tasks" ON care_tasks;
DROP POLICY IF EXISTS "Users can insert own tasks" ON care_tasks;
DROP POLICY IF EXISTS "Users can update own tasks" ON care_tasks;
DROP POLICY IF EXISTS "Users can delete own tasks" ON care_tasks;
CREATE POLICY "Users can view own tasks" ON care_tasks FOR SELECT USING (auth.uid() = user_id);
CREATE POLICY "Users can insert own tasks" ON care_tasks FOR INSERT WITH CHECK (auth.uid() = user_id);
CREATE POLICY "Users can update own tasks" ON care_tasks FOR UPDATE USING (auth.uid() = user_id);
CREATE POLICY "Users can delete own tasks" ON care_tasks FOR DELETE USING (auth.uid() = user_id);

-- ============ WATERING LOGS ============
CREATE TABLE IF NOT EXISTS watering_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    plant_id UUID NOT NULL REFERENCES plants(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    watered_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    notes TEXT
);

ALTER TABLE watering_logs ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Users can view own logs" ON watering_logs;
DROP POLICY IF EXISTS "Users can insert own logs" ON watering_logs;
CREATE POLICY "Users can view own logs" ON watering_logs FOR SELECT USING (auth.uid() = user_id);
CREATE POLICY "Users can insert own logs" ON watering_logs FOR INSERT WITH CHECK (auth.uid() = user_id);

-- ============ SENSOR READINGS ============
CREATE TABLE IF NOT EXISTS sensor_readings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    plant_id UUID REFERENCES plants(id) ON DELETE SET NULL,
    zone TEXT NOT NULL,
    temperature REAL,
    humidity REAL,
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

ALTER TABLE sensor_readings ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Users can view own readings" ON sensor_readings;
DROP POLICY IF EXISTS "Users can insert own readings" ON sensor_readings;
CREATE POLICY "Users can view own readings" ON sensor_readings FOR SELECT USING (auth.uid() = user_id);
CREATE POLICY "Users can insert own readings" ON sensor_readings FOR INSERT WITH CHECK (auth.uid() = user_id);

-- ============ MAVJUD KETMA-KETLIKKA QO'SHIMCHA MA'LUMOTLAR QO'SHISH ============
-- Agar jadvallar avvaldan mavjud bo'lgan bo'lsa, ularga kerakli yangi column lar qo'shamiz
ALTER TABLE plants ADD COLUMN IF NOT EXISTS watering_interval_hours INT NOT NULL DEFAULT 24;
ALTER TABLE plants ADD COLUMN IF NOT EXISTS zone TEXT;
ALTER TABLE plants ADD COLUMN IF NOT EXISTS notes TEXT;

ALTER TABLE care_tasks ADD COLUMN IF NOT EXISTS plant_id UUID REFERENCES plants(id) ON DELETE SET NULL;

ALTER TABLE sensor_readings ADD COLUMN IF NOT EXISTS plant_id UUID REFERENCES plants(id) ON DELETE SET NULL;

-- ============ INDEKSLAR ============
CREATE INDEX IF NOT EXISTS idx_plants_user ON plants(user_id);
CREATE INDEX IF NOT EXISTS idx_tasks_user_date ON care_tasks(user_id, scheduled_date);
CREATE INDEX IF NOT EXISTS idx_tasks_plant ON care_tasks(plant_id);
CREATE INDEX IF NOT EXISTS idx_sensor_user ON sensor_readings(user_id, recorded_at DESC);
CREATE INDEX IF NOT EXISTS idx_sensor_plant ON sensor_readings(plant_id);
CREATE INDEX IF NOT EXISTS idx_watering_plant ON watering_logs(plant_id, watered_at DESC);

-- ============ TRIGGER: Sug'orilganda health oshadi ============
CREATE OR REPLACE FUNCTION update_plant_health()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE plants 
    SET health_status = LEAST(100, health_status + 5),
        last_watered = now()
    WHERE id = NEW.plant_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS on_plant_watered ON watering_logs;
CREATE TRIGGER on_plant_watered
    AFTER INSERT ON watering_logs
    FOR EACH ROW EXECUTE FUNCTION update_plant_health();
