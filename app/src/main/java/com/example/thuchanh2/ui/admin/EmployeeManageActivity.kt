package com.example.thuchanh2.ui.admin

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thuchanh2.data.AppDatabase
import com.example.thuchanh2.databinding.ActivityEmployeeManagementBinding

class EmployeeManageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmployeeManagementBinding
    private lateinit var adapter: EmployeeAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Quản lý Nhân viên"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = AppDatabase.getInstance(this)

        adapter = EmployeeAdapter(emptyList()) { emp ->
            val intent = Intent(this, EmployeeFormActivity::class.java)
            intent.putExtra("EMP_ID", emp.id)
            startActivity(intent)
        }

        binding.rvEmployees.layoutManager = LinearLayoutManager(this)
        binding.rvEmployees.adapter = adapter

        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, EmployeeFormActivity::class.java))
        }

        binding.btnSalaryManagement.setOnClickListener {
            startActivity(Intent(this, AdminSalaryActivity::class.java))
        }

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val keyword = s.toString()
                if (keyword.isEmpty()) loadData()
                else adapter.updateData(db.employeeDao().searchEmployees(keyword))
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        adapter.updateData(db.employeeDao().getAllEmployees())
    }
}
