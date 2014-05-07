# encoding: UTF-8
# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema.define(version: 20140507072725) do

  # These are extensions that must be enabled in order to support this database
  enable_extension "plpgsql"

  create_table "campaigns", force: true do |t|
    t.integer  "carrier_id"
    t.string   "name"
    t.string   "description"
    t.date     "start_date"
    t.date     "end_date"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "carriers", force: true do |t|
    t.string   "name"
    t.string   "phoneno"
    t.string   "description"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "prizes", force: true do |t|
    t.string   "name"
    t.string   "description"
    t.boolean  "valid"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "campaign_id"
  end

  add_index "prizes", ["campaign_id"], name: "index_prizes_on_campaign_id", using: :btree

  create_table "users", force: true do |t|
    t.string   "name"
    t.string   "email"
    t.string   "phoneno"
    t.string   "phoneid"
    t.date     "birtdate"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

end
