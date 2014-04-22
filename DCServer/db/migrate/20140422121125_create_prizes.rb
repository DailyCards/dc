class CreatePrizes < ActiveRecord::Migration
  def change
    create_table :prizes do |t|
      t.string :name
      t.references :carrier
      t.string :description
      t.boolean :valid

      t.timestamps
    end
  end
end
