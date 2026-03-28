-- =============================================
-- HotRoom Supabase Database Schema v3
-- Multi-Tenancy (Greenhouse), Roles and Task Tracking
-- =============================================

-- ============ 1-BOSQICH: JADVALLARNI YARATISH ============

CREATE TABLE IF NOT EXISTS greenhouses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL DEFAULT 'Mening issiqxonam',
    owner_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

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
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS care_tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
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

CREATE TABLE IF NOT EXISTS watering_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    plant_id UUID NOT NULL REFERENCES plants(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    watered_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    notes TEXT
);

CREATE TABLE IF NOT EXISTS sensor_readings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    zone TEXT NOT NULL,
    temperature REAL,
    humidity REAL,
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============ 2-BOSQICH: MAVJUD JADVALLARGA YANGI USTUNLAR QO'SHISH (MIGRATION) ============
-- Bu qatorlar bazada bo'lmasa qoshadi va keyingi bosqichlardagi policy larni buzilishidan asraydi

ALTER TABLE profiles ADD COLUMN IF NOT EXISTS greenhouse_id UUID REFERENCES greenhouses(id) ON DELETE SET NULL;

ALTER TABLE plants ADD COLUMN IF NOT EXISTS watering_interval_hours INT NOT NULL DEFAULT 24;
ALTER TABLE plants ADD COLUMN IF NOT EXISTS zone TEXT;
ALTER TABLE plants ADD COLUMN IF NOT EXISTS notes TEXT;
ALTER TABLE plants ADD COLUMN IF NOT EXISTS greenhouse_id UUID REFERENCES greenhouses(id) ON DELETE CASCADE;

ALTER TABLE care_tasks ADD COLUMN IF NOT EXISTS plant_id UUID REFERENCES plants(id) ON DELETE CASCADE;
ALTER TABLE care_tasks ADD COLUMN IF NOT EXISTS greenhouse_id UUID REFERENCES greenhouses(id) ON DELETE CASCADE;
ALTER TABLE care_tasks ADD COLUMN IF NOT EXISTS completed_by UUID REFERENCES profiles(id) ON DELETE SET NULL;

ALTER TABLE watering_logs ADD COLUMN IF NOT EXISTS greenhouse_id UUID REFERENCES greenhouses(id) ON DELETE CASCADE;
ALTER TABLE watering_logs ADD COLUMN IF NOT EXISTS completed_by UUID REFERENCES profiles(id) ON DELETE SET NULL;

ALTER TABLE sensor_readings ADD COLUMN IF NOT EXISTS plant_id UUID REFERENCES plants(id) ON DELETE SET NULL;
ALTER TABLE sensor_readings ADD COLUMN IF NOT EXISTS greenhouse_id UUID REFERENCES greenhouses(id) ON DELETE CASCADE;


-- ============ 3-BOSQICH: ESKI MA'LUMOTLARNI AVTOMAT ULAB QO'YISH ============
INSERT INTO greenhouses (name, owner_id)
SELECT 'Eski Issiqxona', id FROM profiles WHERE greenhouse_id IS NULL AND id IN (SELECT id FROM auth.users)
ON CONFLICT DO NOTHING;

UPDATE profiles SET greenhouse_id = (SELECT id FROM greenhouses WHERE owner_id = profiles.id LIMIT 1) WHERE greenhouse_id IS NULL;
UPDATE plants SET greenhouse_id = (SELECT greenhouse_id FROM profiles WHERE profiles.id = plants.user_id LIMIT 1) WHERE greenhouse_id IS NULL;
UPDATE care_tasks SET greenhouse_id = (SELECT greenhouse_id FROM profiles WHERE profiles.id = care_tasks.user_id LIMIT 1) WHERE greenhouse_id IS NULL;
UPDATE watering_logs SET greenhouse_id = (SELECT greenhouse_id FROM profiles WHERE profiles.id = watering_logs.user_id LIMIT 1) WHERE greenhouse_id IS NULL;
UPDATE sensor_readings SET greenhouse_id = (SELECT greenhouse_id FROM profiles WHERE profiles.id = sensor_readings.user_id LIMIT 1) WHERE greenhouse_id IS NULL;


-- ============ 4-BOSQICH: XAVFSIZLIK VA RLS (POLICIES) O'RNATISH ============

ALTER TABLE greenhouses ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Authenticated users view greenhouses" ON greenhouses;
CREATE POLICY "Authenticated users view greenhouses" ON greenhouses FOR SELECT USING (auth.role() = 'authenticated');
DROP POLICY IF EXISTS "Owner inserts greenhouse" ON greenhouses;
CREATE POLICY "Owner inserts greenhouse" ON greenhouses FOR INSERT WITH CHECK (auth.uid() = owner_id);

ALTER TABLE profiles ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Users can view own profile" ON profiles;
DROP POLICY IF EXISTS "Users can update own profile" ON profiles;
DROP POLICY IF EXISTS "Profiles are viewable by authenticated users" ON profiles;
CREATE POLICY "Profiles are viewable by authenticated users" ON profiles FOR SELECT USING (auth.role() = 'authenticated');
CREATE POLICY "Users can update own profile" ON profiles FOR UPDATE USING (auth.uid() = id);

ALTER TABLE plants ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Users can view own plants" ON plants;
DROP POLICY IF EXISTS "Users can insert own plants" ON plants;
DROP POLICY IF EXISTS "Users can update own plants" ON plants;
DROP POLICY IF EXISTS "Users can delete own plants" ON plants;
DROP POLICY IF EXISTS "Team can view greenhouse plants" ON plants;
DROP POLICY IF EXISTS "Team can insert greenhouse plants" ON plants;
DROP POLICY IF EXISTS "Team can update greenhouse plants" ON plants;
DROP POLICY IF EXISTS "Team can delete greenhouse plants" ON plants;

CREATE POLICY "Team can view greenhouse plants" ON plants FOR SELECT 
USING (greenhouse_id = (SELECT greenhouse_id FROM profiles WHERE id = auth.uid()) OR auth.uid() = user_id);
CREATE POLICY "Team can insert greenhouse plants" ON plants FOR INSERT 
WITH CHECK (greenhouse_id = (SELECT greenhouse_id FROM profiles WHERE id = auth.uid()) OR auth.uid() = user_id);
CREATE POLICY "Team can update greenhouse plants" ON plants FOR UPDATE 
USING (greenhouse_id = (SELECT greenhouse_id FROM profiles WHERE id = auth.uid()) OR auth.uid() = user_id);
CREATE POLICY "Team can delete greenhouse plants" ON plants FOR DELETE 
USING (greenhouse_id = (SELECT greenhouse_id FROM profiles WHERE id = auth.uid()) OR auth.uid() = user_id);

ALTER TABLE care_tasks ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Users can view own tasks" ON care_tasks;
DROP POLICY IF EXISTS "Users can insert own tasks" ON care_tasks;
DROP POLICY IF EXISTS "Users can update own tasks" ON care_tasks;
DROP POLICY IF EXISTS "Users can delete own tasks" ON care_tasks;
DROP POLICY IF EXISTS "Team can view greenhouse tasks" ON care_tasks;
DROP POLICY IF EXISTS "Team can insert greenhouse tasks" ON care_tasks;
DROP POLICY IF EXISTS "Team can update greenhouse tasks" ON care_tasks;
DROP POLICY IF EXISTS "Team can delete greenhouse tasks" ON care_tasks;

CREATE POLICY "Team can view greenhouse tasks" ON care_tasks FOR SELECT 
USING (greenhouse_id = (SELECT greenhouse_id FROM profiles WHERE id = auth.uid()) OR auth.uid() = user_id);
CREATE POLICY "Team can insert greenhouse tasks" ON care_tasks FOR INSERT 
WITH CHECK (greenhouse_id = (SELECT greenhouse_id FROM profiles WHERE id = auth.uid()) OR auth.uid() = user_id);
CREATE POLICY "Team can update greenhouse tasks" ON care_tasks FOR UPDATE 
USING (greenhouse_id = (SELECT greenhouse_id FROM profiles WHERE id = auth.uid()) OR auth.uid() = user_id);
CREATE POLICY "Team can delete greenhouse tasks" ON care_tasks FOR DELETE 
USING (greenhouse_id = (SELECT greenhouse_id FROM profiles WHERE id = auth.uid()) OR auth.uid() = user_id);


ALTER TABLE watering_logs ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Users can view own logs" ON watering_logs;
DROP POLICY IF EXISTS "Users can insert own logs" ON watering_logs;
DROP POLICY IF EXISTS "Team can view greenhouse logs" ON watering_logs;
DROP POLICY IF EXISTS "Team can insert greenhouse logs" ON watering_logs;

CREATE POLICY "Team can view greenhouse logs" ON watering_logs FOR SELECT 
USING (greenhouse_id = (SELECT greenhouse_id FROM profiles WHERE id = auth.uid()) OR auth.uid() = user_id);
CREATE POLICY "Team can insert greenhouse logs" ON watering_logs FOR INSERT 
WITH CHECK (greenhouse_id = (SELECT greenhouse_id FROM profiles WHERE id = auth.uid()) OR auth.uid() = user_id);


ALTER TABLE sensor_readings ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Users can view own readings" ON sensor_readings;
DROP POLICY IF EXISTS "Users can insert own readings" ON sensor_readings;
DROP POLICY IF EXISTS "Team can view greenhouse readings" ON sensor_readings;
DROP POLICY IF EXISTS "Team can insert greenhouse readings" ON sensor_readings;

CREATE POLICY "Team can view greenhouse readings" ON sensor_readings FOR SELECT 
USING (greenhouse_id = (SELECT greenhouse_id FROM profiles WHERE id = auth.uid()) OR auth.uid() = user_id);
CREATE POLICY "Team can insert greenhouse readings" ON sensor_readings FOR INSERT 
WITH CHECK (greenhouse_id = (SELECT greenhouse_id FROM profiles WHERE id = auth.uid()) OR auth.uid() = user_id);


-- ============ 5-BOSQICH: TRIGGERS VA FUNKSIYALAR ============

CREATE OR REPLACE FUNCTION handle_new_user()
RETURNS TRIGGER AS $$
DECLARE
    new_gh_id UUID;
BEGIN
    INSERT INTO greenhouses (name, owner_id) VALUES ('Mening issiqxonam', NEW.id) RETURNING id INTO new_gh_id;
    INSERT INTO profiles (id, name, role, greenhouse_id)
    VALUES (NEW.id, COALESCE(NEW.raw_user_meta_data->>'name', ''), 'admin', new_gh_id);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
    AFTER INSERT ON auth.users
    FOR EACH ROW EXECUTE FUNCTION handle_new_user();


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

-- ============ INDEKSLAR ============
CREATE INDEX IF NOT EXISTS idx_plants_gh ON plants(greenhouse_id);
CREATE INDEX IF NOT EXISTS idx_tasks_gh_date ON care_tasks(greenhouse_id, scheduled_date);
CREATE INDEX IF NOT EXISTS idx_sensor_gh ON sensor_readings(greenhouse_id, recorded_at DESC);
CREATE INDEX IF NOT EXISTS idx_watering_gh ON watering_logs(greenhouse_id, watered_at DESC);
